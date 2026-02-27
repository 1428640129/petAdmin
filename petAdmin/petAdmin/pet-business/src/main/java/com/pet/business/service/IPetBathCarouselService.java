package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathCarousel;

/**
 * 轮播图Service接口
 * 
 * @author Pet
 */
public interface IPetBathCarouselService
{
    /**
     * 查询轮播图
     * 
     * @param carouselId 轮播图主键
     * @return 轮播图
     */
    public PetBathCarousel selectBathCarouselByCarouselId(Long carouselId);

    /**
     * 查询轮播图列表
     * 
     * @param bathCarousel 轮播图
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
     * @param bathCarousel 轮播图
     * @return 结果
     */
    public int insertBathCarousel(PetBathCarousel bathCarousel);

    /**
     * 修改轮播图
     * 
     * @param bathCarousel 轮播图
     * @return 结果
     */
    public int updateBathCarousel(PetBathCarousel bathCarousel);

    /**
     * 批量删除轮播图
     * 
     * @param carouselIds 需要删除的轮播图主键集合
     * @return 结果
     */
    public int deleteBathCarouselByCarouselIds(Long[] carouselIds);

    /**
     * 删除轮播图信息
     * 
     * @param carouselId 轮播图主键
     * @return 结果
     */
    public int deleteBathCarouselByCarouselId(Long carouselId);
}










