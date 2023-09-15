package cc.powind.huron.core.router;

import cc.powind.huron.core.exception.RouteNotMatchException;
import cc.powind.huron.core.model.Realtime;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpRouter extends AbstractRouter implements Servlet {

    private static final String HTTP_ALIAS_PARAMETER_NAME = "aliasName";
    private static final String HTTP_BATCH_CHECK_PARAMETER_NAME = "batch";

    private ObjectMapper mapper;

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private final GenericServlet genericServlet = new GenericServlet() {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

            try {

                route((HttpServletRequest) req, (HttpServletResponse) res);

            } catch (RouteNotMatchException e) {

                log.error("remote address: " + req.getRemoteAddr() + " request illegal");
            } catch (IllegalArgumentException e) {

                log.error(e.getMessage());
            }
        }
    };

    public void route(HttpServletRequest request, HttpServletResponse response) throws IOException, RouteNotMatchException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        // check if batch save
        boolean isBatch = checkIfBatch(request);

        // check the clazz
        Class<? extends Realtime> clazz = getRealtimeClazz(request);
        if (clazz == null) {
            throw new RouteNotMatchException();
        }

        // get the request body
        String jsonText = read(request.getInputStream());
        if ("".equals(jsonText)) {
            throw new IllegalArgumentException("request body is null");
        }

        if (isBatch) {

            List<Realtime> realtimeList = mapper.readValue(jsonText, mapper.getTypeFactory().constructCollectionType(List.class, clazz));

            realtimeList.forEach(this::collect);
        } else {

            Realtime realtime = mapper.readValue(jsonText, clazz);

            collect(realtime);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        genericServlet.init(config);
    }

    @Override
    public ServletConfig getServletConfig() {
        return genericServlet.getServletConfig();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        genericServlet.service(req, res);
    }

    @Override
    public String getServletInfo() {
        return genericServlet.getServletInfo();
    }

    @Override
    public void destroy() {
        genericServlet.destroy();
    }

    private boolean checkIfBatch(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] parameterValue = parameterMap.get(HTTP_BATCH_CHECK_PARAMETER_NAME);
        return ArrayUtils.isNotEmpty(parameterValue) && ("1".equals(parameterValue[0]) || "true".equals(parameterValue[0]));
    }

    private Class<? extends Realtime> getRealtimeClazz(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] parameterValue = parameterMap.get(HTTP_ALIAS_PARAMETER_NAME);
        if (ArrayUtils.isNotEmpty(parameterValue)) {
            return getRealtimeRegister().getClazz(parameterValue[0]);
        }

        return null;
    }

    private String read(InputStream inputStream) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] bytes = new byte[1024];

        int n;
        while ((n = inputStream.read(bytes)) != -1) {
            os.write(bytes, 0, n);
        }

        os.close();

        return new String(os.toByteArray(), StandardCharsets.UTF_8);
    }
}
