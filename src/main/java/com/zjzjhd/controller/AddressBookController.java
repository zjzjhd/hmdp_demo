package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjzjhd.common.BaseContext;
import com.zjzjhd.common.R;
import com.zjzjhd.entity.AddressBook;
import com.zjzjhd.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 地址簿管理
 * @date 2022/11/18 19:28
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * @description: 显示当前用户所有的地址
     * @param: [addressBook]
     * @return: R<List < AddressBook>>
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {

        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(queryWrapper));

    }

    /**
     * @description: 新增
     * @param: [addressBook]
     * @return: R<String>
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        //设置当前用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("新增地址成功");
    }

    /**
     * @description: 设置默认地址
     * @param: [addressBook]
     * @return: R<AddressBook>
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * @description: 根据id查询地址
     * @param: [id]
     * @return: R<AddressBook>
     */
    @GetMapping("/{id}")
    public R<AddressBook> update(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * @description: 删除地址
     * @param: [ids]
     * @return: R<String>
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long ids) {
        addressBookService.removeById(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * @description: 更新地址
     * @param: [addressBook]
     * @return: R<String>
     */

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        //设置当前用户id
        addressBookService.updateById(addressBook);
        return R.success("修改地址成功");
    }

    /**
     * @description: 显示默认地址
     * @param: []
     * @return: R<AddressBook>
     */
    @GetMapping("/default")
    public R<AddressBook> getAddress() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, currentId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        return R.success(one);

    }
}
