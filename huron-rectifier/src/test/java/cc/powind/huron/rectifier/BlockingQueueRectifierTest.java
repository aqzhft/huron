package cc.powind.huron.rectifier;

import org.junit.Test;

public class BlockingQueueRectifierTest {

    @Test
    public void test() throws Exception {
        Rectifier<String> rectifier = new BlockingQueueRectifier<>();
        rectifier.outflow(strings -> {
            System.out.println(" consume ====> " + strings);
        });

        rectifier.inflow("hello");
        rectifier.inflow("world");

        System.in.read();
    }

}