package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pet.business.mapper.PetBathCarouselMapper;
import com.pet.system.domain.PetBathCarousel;
import com.pet.business.service.IPetBathCarouselService;

/**
 * 轮播图Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathCarouselServiceImpl implements IPetBathCarouselService
{
    @Autowired
    private PetBathCarouselMapper bathCarouselMapper;

    /**
     * 查询轮播图
     * 
     * @param carouselId 轮播图主键
     * @return 轮播图
     */
    @Override
    public PetBathCarousel selectBathCarouselByCarouselId(Long carouselId)
    {
        return bathCarouselMapper.selectBathCarouselById(carouselId);
    }

    /**
     * 查询轮播图列表
     * 
     * @param bathCarousel 轮播图
     * @return 轮播图
     */
    @Override
    public List<PetBathCarousel> selectBathCarouselList(PetBathCarousel bathCarousel)
    {
        return bathCarouselMapper.selectBathCarouselList(bathCarousel);
    }

    /**
     * 查询启用的轮播图列表（用于前端展示）
     * 
     * @return 轮播图集合
     */
    @Override
    public List<PetBathCarousel> selectEnabledBathCarouselList()
    {
        return bathCarouselMapper.selectEnabledBathCarouselList();
    }

    /**
     * 新增轮播图
     * 
     * @param bathCarousel 轮播图
     * @return 结果
     */
    @Override
    public int insertBathCarousel(PetBathCarousel bathCarousel)
    {
        return bathCarouselMapper.insertBathCarousel(bathCarousel);
    }

    /**
     * 修改轮播图
     * 
     * @param bathCarousel 轮播图
     * @return 结果
     */
    @Override
    public int updateBathCarousel(PetBathCarousel bathCarousel)
    {
        return bathCarouselMapper.updateBathCarousel(bathCarousel);
    }

    /**
     * 批量删除轮播图
     * 
     * @param carouselIds 需要删除的轮播图主键
     * @return 结果
     */
    @Override
    public int deleteBathCarouselByCarouselIds(Long[] carouselIds)
    {
        return bathCarouselMapper.deleteBathCarouselByIds(carouselIds);
    }

    /**
     * 删除轮播图信息
     * 
     * @param carouselId 轮播图主键
     * @return 结果
     */
    @Override
    public int deleteBathCarouselByCarouselId(Long carouselId)
    {
        return bathCarouselMapper.deleteBathCarouselById(carouselId);
    }
}










