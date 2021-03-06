package tourGuide.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public final class MultiTaskService {

  public static final ExecutorService executorService = Executors.newFixedThreadPool(195);
  public static final List<Future> futures = new ArrayList<>();

  public static <T> Future<T> submit(Callable<T> callable) {
    Future<T> f = executorService.submit(callable);
    futures.add(f);
    return f;
  }

  public static Future<?> submit(Runnable runnable) {
    Future<?> f = executorService.submit(runnable);
    futures.add(f);
    return f;
  }


  public static void clearFutures() {
    futures.clear();
  }
}
