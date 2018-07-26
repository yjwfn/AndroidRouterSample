package im.xingzhe.lib.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface UriDestination {

    String name();


    DestinationUri uri();

    DestinationArgument[] out() default {};

    DestinationArgument[] in() default {};
}
