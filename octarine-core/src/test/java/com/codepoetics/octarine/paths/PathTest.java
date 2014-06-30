package com.codepoetics.octarine.paths;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PathTest {

    @Test
    public void
    paths_compose() {
        Path<Map<String, List<Map<Class<?>, List<String>>>>, String> path =
                Path.<String, List<Map<Class<?>, List<String>>>>toKey("a")
                        .join(Path.toIndex(3))
                        .join(Path.<Class<?>, List<String>>toKey(String.class))
                        .join(Path.toIndex(2))
                        .join(Path.<String, String>to(s -> Optional.of(s.substring(2, 1)), "thirdChar"));

        assertThat(path.describe(), equalTo("['a'][3]['class java.lang.String'][2].thirdChar"));
    }
}
