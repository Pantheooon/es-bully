package cn.pmj.bully.transport.netty.invoke;

import lombok.Data;

import java.util.concurrent.*;

@Data
public class InvokeFuture implements Future<BullyResponse> {

    private CountDownLatch latch = new CountDownLatch(1);

    private BullyRequest request;

    private BullyResponse response;

    private Long timeOut;

    private TimeUnit timeUnit;

    public InvokeFuture(BullyRequest bullyRequest, Long timeOut, TimeUnit timeUnit) {
        this.request = bullyRequest;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        request.setTimeOut(System.currentTimeMillis() + timeUnit.toMillis(timeOut));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }


    @Override
    public boolean isCancelled() {
        return latch.getCount() == 0;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public BullyResponse get() throws InterruptedException, ExecutionException {
        try {
            return get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BullyResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        return response;
    }

}
