package search;

import java.util.List;

public class ThreadedSearch<T> implements Searcher<T>, Runnable {

    private int numThreads;
    private T target;
    private List<T> list;
    private int begin;
    private int end;
    private Answer answer;

    public ThreadedSearch(int numThreads) {
        this.numThreads = numThreads;
    }

    private ThreadedSearch(T target, List<T> list, int begin, int end, Answer answer) {
        this.target = target;
        this.list = list;
        this.begin = begin;
        this.end = end;
        this.answer = answer;
    }

    /**
     * Searches `list` in parallel using `numThreads` threads.
     * <p>
     * You can assume that the list size is divisible by `numThreads`
     */
    public boolean search(T target, List<T> list) throws InterruptedException {

        Answer sharedAnswer = new Answer();

        // Create Arrays to hold the threadedSearch and Thread instances
        ThreadedSearch[] searchArray = new ThreadedSearch[numThreads];
        Thread[] threads = new Thread[numThreads];

        // Loop to initialize the threadedSearches with the appropriate ranges
        for(int i = 0; i < numThreads; i++) {
            searchArray[i] = new ThreadedSearch<T>(target, list, getBeginningRange(i, list.size()), getEndRange(i, list.size()), sharedAnswer);
        }

        // Initializes Threads with the correct threadedSearch and starts them
        for(int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(searchArray[i]);
            threads[i].start();
        }

        // Wait for threads to finish
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        return sharedAnswer.getAnswer();
    }

    public int getBeginningRange(int threadNumber, int listSize) {

        // Provides the size of each range
        int partitionSize = (int)Math.ceil(listSize/numThreads);
        int begin = partitionSize * threadNumber;

        return begin;
    }

    public int getEndRange(int threadNumber, int listSize) {

        int partitionSize = (int)Math.ceil(listSize/numThreads);
        int end;

        // Makes sure the size of the list matches the end range for last thread
        if(threadNumber == numThreads - 1) {
            end = listSize;
        } else {
            end = partitionSize * (threadNumber + 1);
        }
        return end;
    }


    public void run() {
           for (int i = begin; i < end; i++) {
               //  Terminate the thread, the list has the target
               if (answer.getAnswer() == true){ break; }

               // Set the shared answer to true to return the answer and
               // Tell other threads to terminate
               if (list.get(i).equals(target)) {
                   answer.setAnswer(true);
               }
           }

    }

    private class Answer {
        private boolean answer = false;

        public boolean getAnswer() { return answer; }

        public synchronized void setAnswer(boolean newAnswer) {
            answer = newAnswer;
        }
    }

}
