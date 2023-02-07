package cc.powind.huron.core.router;

import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.GenericServlet;

public abstract class AbstractRouter {

    protected final Log log = LogFactory.getLog(getClass());

    private CollectService collectService;

    private RealtimeRegister realtimeRegister;

    public CollectService getCollectService() {
        return collectService;
    }

    public void setCollectService(CollectService collectService) {
        this.collectService = collectService;
    }

    protected void collect(Realtime realtime) {
        collectService.collect(realtime);
    }

    public RealtimeRegister getRealtimeRegister() {
        return realtimeRegister;
    }

    public void setRealtimeRegister(RealtimeRegister realtimeRegister) {
        this.realtimeRegister = realtimeRegister;
    }
}
