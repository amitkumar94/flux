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

package com.flipkart.flux.domain;

import java.util.List;

/**
 * <code>StateMachine</code> represents a state machine submitted for execution in Flux.
 * Maintains meta data about the current state of execution of a state machine
 * 
 * @author Yogesh
 * @author regunath.balasubramanian
 * @author shyam.akirala
 */
public class StateMachine<T> {

    /* provided */
	/** The version identifier*/
    private Long version;
    /** Name for this state machine*/
    private String name;
    /** Description of the state machine*/
    private String description;
    /** List of states that this machine has*/
    private List<State<T>> states;
    /** The start state for this machine */
    private State<T> startState;

    /* maintained */
    /** Current state of this state machine*/
    private State<T> currentState;
    /** The Context for interacting with the Flux runtime*/
    private Context<T> context;
    
    /** Constructor*/
	public StateMachine(Long version, String name, String description, List<State<T>> states, State<T> startState) {
		super();
		this.version = version;
		this.name = name;
		this.description = description;
		this.states = states;
		this.startState = startState;
	}

	/** Accessor/Mutator methods */
	public Context<T> getContext() {
		return context;
	}
	public void setContext(Context<T> context) {
		this.context = context;
	}
	public State<T> getCurrentState() {
		return currentState;
	}
	public void setCurrentState(State<T> currentState) {
		this.currentState = currentState;
	}
	public Long getVersion() {
		return version;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public List<State<T>> getStates() {
		return states;
	}
	public State<T> getStartState() {
		return startState;
	}

}