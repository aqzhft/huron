package cc.powind.huron.assemble.properties;

public class RouterProperties {

    private Http http = new Http();

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public static class Http {

        private String urlPattern = "/realtime";

        public String getUrlPattern() {
            return urlPattern;
        }

        public void setUrlPattern(String urlPattern) {
            this.urlPattern = urlPattern;
        }
    }
}
