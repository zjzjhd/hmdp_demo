package com.zjzjhd.dto;


import com.zjzjhd.entity.Setmeal;
import com.zjzjhd.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
