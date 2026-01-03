package rgonzalez.smbc.contacts.model.system;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface BusinessActivity {
    String lineOfBusiness();

    String activityName();

    String eventName();

    boolean isOnlyAnInquiry() default true;

    boolean isAuditable() default true;
}
