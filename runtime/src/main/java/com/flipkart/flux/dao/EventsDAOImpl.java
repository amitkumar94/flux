/*
 * Copyright 2012-2016, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.flux.dao;

import com.flipkart.flux.api.EventData;
import com.flipkart.flux.dao.iface.EventsDAO;
import com.flipkart.flux.domain.Event;
import com.flipkart.flux.persistence.*;
import com.google.inject.name.Named;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>EventsDAOImpl</code> is an implementation of {@link EventsDAO} which uses Hibernate to perform operations.
 * @author shyam.akirala
 */
public class EventsDAOImpl extends AbstractDAO<Event> implements EventsDAO {

    @Inject
    public EventsDAOImpl(@Named("fluxSessionFactoriesContext") SessionFactoryContext sessionFactoryContext) {
        super(sessionFactoryContext);
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public Event create(String stateMachineInstanceId, Event event) {
        return super.save(event);
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public void updateEvent(String stateMachineInstanceId, Event event) {
        super.update(event);
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public List<Event> findBySMInstanceId(String stateMachineInstanceId) {
        return currentSession().createCriteria(Event.class).add(Restrictions.eq("stateMachineInstanceId", stateMachineInstanceId)).list();
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public Event findById(String stateMachineInstanceId, Long id) {
        return super.findById(Event.class, id);
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public Event findBySMIdAndName(String stateMachineInstanceId, String eventName) {
        Criteria criteria = currentSession().createCriteria(Event.class).add(Restrictions.eq("stateMachineInstanceId", stateMachineInstanceId))
                .add(Restrictions.eq("name", eventName));
        return (Event) criteria.uniqueResult();
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public List<String> findTriggeredEventsNamesBySMId(String stateMachineInstanceId) {
        Criteria criteria = currentSession().createCriteria(Event.class).add(Restrictions.eq("stateMachineInstanceId", stateMachineInstanceId))
                .add(Restrictions.eq("status", Event.EventStatus.triggered))
                .setProjection(Projections.property("name"));
        return criteria.list();
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public List<Event> findTriggeredEventsBySMId(String stateMachineInstanceId) {
        Criteria criteria = currentSession().createCriteria(Event.class).add(Restrictions.eq("stateMachineInstanceId", stateMachineInstanceId))
                .add(Restrictions.eq("status", Event.EventStatus.triggered));
        return criteria.list();
    }

    @Override
    @Transactional
    @SelectDataSource(type = DataSourceType.READ_WRITE, storage = storage.SHARDED)
    public List<EventData> findByEventNamesAndSMId( String stateMachineInstanceId, List<String> eventNames) {
        if (eventNames.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder eventNamesString = new StringBuilder();
        for(int i=0; i<eventNames.size(); i++) {
            eventNamesString.append("\'"+eventNames.get(i)+"\'");
            if(i!=eventNames.size()-1)
                eventNamesString.append(", ");
        }
        //retrieves and returns the events in the order of eventNames
        Query hqlQuery = currentSession().createQuery("from Event where stateMachineInstanceId = :SMID and name in (" + eventNamesString.toString()
                + ") order by field(name, " + eventNamesString.toString() + ")").setParameter("SMID", stateMachineInstanceId);
        List<Event> readEvents = hqlQuery.list();
        LinkedList<EventData> readEventsDTOs = new LinkedList<EventData>();
        for (Event event : readEvents) {
        	readEventsDTOs.add(new EventData(event.getName(), event.getType(),event.getEventData(), event.getEventSource()));
        }
        return readEventsDTOs;
    }

}
