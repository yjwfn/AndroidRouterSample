package im.xingzhe.lib.router;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

public final class Utils {

    public static CaseFormat resolveCaseFormat(String name) {
        if (name == null || Strings.isNullOrEmpty(name)) {
            throw new NullPointerException("name == null");
        }

        CharMatcher charMatcher = CharMatcher.is('_');
        int index = charMatcher.indexIn(name);

        return index != -1 ? CaseFormat.LOWER_UNDERSCORE : CaseFormat.LOWER_CAMEL;
    }
}
