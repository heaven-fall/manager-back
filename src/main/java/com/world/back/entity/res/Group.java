package com.world.back.entity.res;

import lombok.Data;

@Data
public class Group
{
    int id;
    String admin_id;
    int year;
    int student_count;
    int max_student_count;
}
