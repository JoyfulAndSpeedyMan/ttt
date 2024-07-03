import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.GrpcExchanger;
import com.bolingx.ai.worker.grpc.ITaskProcessor;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class TestGrpcClient {

    @Test
    public void test() throws InterruptedException {
        GrpcExchanger grpcExchanger = new GrpcExchanger("127.0.0.1", 9251, List.of(""));
        grpcExchanger.setIReportStatus(() -> AiWorkerOuterClass.WorkerStatus.newBuilder()
                .setStateOfLife(AiWorkerOuterClass.StateOfLife.STATE_OF_LIFE_OK)
                .build());
        grpcExchanger.setITaskProcessor((value, serverStreamObserver) -> {
            log.info("onTask {}", value.toString());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            serverStreamObserver.onNext(AiWorkerOuterClass.ServerRpcCallback.newBuilder()
                    .setFlag(0)
                    .setReqId(value.getReqId())
                    .setData("demo test data")
                    .setError(false)
                    .build());

        });
        grpcExchanger.start();
        Thread.currentThread().join();

    }
}
