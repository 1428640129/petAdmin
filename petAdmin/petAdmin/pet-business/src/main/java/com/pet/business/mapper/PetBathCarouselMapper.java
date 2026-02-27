package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathCarousel;

/**
 * 轮播图 数据层
 * 
 * @author Pet
 */
public interface PetBathCarouselMapper
{
    /**
     * 查询轮播图信息
     * 
     * @param carouselId 轮播图ID
     * @return 轮播图信息
     */
    public PetBathCarousel selectBathCarouselById(Long carouselId);

    /**
     * 查询轮播图列表
     * 
     * @param bathCarousel 轮播图信息
     * @return 轮播图集合
     */
    public List<PetBathCarousel> selectBathCarouselList(PetBathCarousel bathCarousel);

    /**
     * 查询启用的轮播图列表（用于前端展示）
     * 
     * @return 轮播图集合
     */
    public List<PetBathCarousel> selectEnabledBathCarouselList();

    /**
     * 新增轮播图
     * 
     * @param bathCarousel 轮播图信息
     * @return 结果
     */
    public int insertBathCarousel(PetBathCarousel bathCarousel);

    /**
     * 修改轮播图
     * 
     * @param bathCarousel 轮播图信息
     * @return 结果
     */
    public int updateBathCarousel(PetBathCarousel bathCarousel);

    /**
     * 删除轮播图
     * 
     * @param carouselId 轮播图ID
     * @return 结果
     */
    public int deleteBathCarouselById(Long carouselId);

    /**
     * 批量删除轮播图
     * 
     * @param carouselIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathCarouselByIds(Long[] carouselIds);
}

