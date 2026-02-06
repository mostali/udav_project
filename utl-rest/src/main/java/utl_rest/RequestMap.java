package utl_rest;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mpc.exception.NI;

import javax.servlet.ServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class RequestMap implements Map<String, String> {

    public final ServletRequest request;

    private Map<String, String[]> params;

    public Map<String, String[]> getParameterMap() {
        return params == null ? params = request.getParameterMap() : params;
    }

    public static RequestMap of(ServletRequest request) {
        return new RequestMap(request);
    }

    @Override
    public int size() {
        return getParameterMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getParameterMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof CharSequence) {
            return request.getParameter(key.toString()) != null;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new NI();
    }

    @Override
    public String get(Object key) {
        if (key instanceof CharSequence) {
            return request.getParameter(key.toString());
        }
        return null;
    }

    @Nullable
    @Override
    public String put(String key, String value) {
        return null;
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return getParameterMap().keySet();
    }

    @NotNull
    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
