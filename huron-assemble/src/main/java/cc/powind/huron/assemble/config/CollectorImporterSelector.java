package cc.powind.huron.assemble.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class CollectorImporterSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {
                BasicConfiguration.class.getName(),
                CollectConfiguration.class.getName(),
                MetricHandlerConfiguration.class.getName(),
                RealtimeFilterConfiguration.class.getName(),
                RealtimeRouterConfiguration.class.getName(),
                RealtimeStorageConfiguration.class.getName(),
                RealtimeValidatorConfiguration.class.getName(),
                RealtimeRouterConfiguration.class.getName()
        };
    }
}
