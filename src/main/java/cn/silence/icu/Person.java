package cn.silence.icu;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author Eych4o
 * @version 1.0.0
 * @since 2026/02/05 20:29 周四
 */
public final class Person {
    @JsonProperty("name")
    private final String name;

    @JsonProperty("employeeId")
    private final String employeeId;

    // Jackson 反序列化必需
    private Person() {
        this.name = "";
        this.employeeId = "";
    }

    public Person(String name, String employeeId) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("姓名不能为空");
        if (employeeId == null || employeeId.trim().isEmpty())
            throw new IllegalArgumentException("工号不能为空");
        this.name = name.trim();
        this.employeeId = employeeId.trim();
    }

    public String getName() {
        return name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        return Objects.equals(employeeId, ((Person) o).employeeId); // 工号唯一标识
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, employeeId);
    }
}
