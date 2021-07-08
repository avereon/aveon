package com.avereon.aveon;

import com.avereon.skill.RunPauseResettable;
import lombok.CustomLog;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@CustomLog
public abstract class FlowSolver implements RunPauseResettable {

	private final Flow2D flow;

	private final Set<Future<?>> tasks;

	private ExecutorService executor;

	public FlowSolver( Flow2D flow, ExecutorService executor ) {
		this.flow = flow;
		this.executor = executor;
		this.tasks = new CopyOnWriteArraySet<>();

		this.flow.setFlowSolver( this );
	}

	public boolean isRunning() {
		return tasks.size() > 0;
	}

	public void run() {
		// Implement in subclass
	}

	public void pause() {
		tasks.forEach( t -> t.cancel( true ) );
	}

	public void reset() {
		pause();
		waitForTasks( true );
		flow.reset();
	}

	/**
	 * Wait for the tasks to complete, cancelled or otherwise
	 */
	protected void waitForTasks( boolean ignoreCancelled ) {
		tasks.forEach( t -> {
			try {
				t.get();
			} catch( CancellationException exception ) {
				if( !ignoreCancelled ) log.atWarn( exception ).log();
			} catch( Exception exception ) {
				log.atWarn( exception ).log();
			}
		} );
	}

}
