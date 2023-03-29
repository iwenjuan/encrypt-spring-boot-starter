package cn.iwenjuan.encrypt.sample.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author li1244
 * @date 2023/3/29 13:40
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class User {

    private String name;

    private int age;
}
