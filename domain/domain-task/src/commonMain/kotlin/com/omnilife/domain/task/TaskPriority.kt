package com.omnilife.domain.task

/**
 * Task priority (TASK-006): exactly 3 levels, deliberately no more — more
 * levels means decision paralysis (Product Bible P28). Ordinal order is
 * ascending urgency (NONE < MEDIUM < HIGH), used by the default sort
 * (TASK-R-02).
 */
public enum class TaskPriority {
    NONE,
    MEDIUM,
    HIGH,
}
