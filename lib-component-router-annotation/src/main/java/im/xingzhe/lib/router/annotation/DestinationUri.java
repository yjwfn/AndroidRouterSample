package im.xingzhe.lib.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.ANNOTATION_TYPE)
public @interface DestinationUri {

    String authority() default "imxingzhe.com";

    String scheme() default "xingzhe";

    String path() default "";

}
