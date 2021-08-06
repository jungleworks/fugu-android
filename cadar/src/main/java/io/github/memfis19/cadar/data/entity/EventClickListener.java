package io.github.memfis19.cadar.data.entity;

import java.io.Serializable;

public interface EventClickListener extends Serializable {
    void onEventClick(Event event);
}
