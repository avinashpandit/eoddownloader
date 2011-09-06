package com.avi.util;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by IntelliJ IDEA.
 * User: avinash
 * Date: Jul 4, 2009
 * Time: 10:24:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class CallerRunPolicy implements RejectedExecutionHandler
{
    /**
     * Creates a <tt>CallerRunsPolicy</tt>.
     */
    public CallerRunPolicy()
    {
    }

    /**
     * Executes task r in the caller's thread, unless the executor
     * has been shut down, in which case the task is discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution( Runnable r, ThreadPoolExecutor e )
    {
        if ( !e.isShutdown() )
        {
            r.run();
        }
    }
}
