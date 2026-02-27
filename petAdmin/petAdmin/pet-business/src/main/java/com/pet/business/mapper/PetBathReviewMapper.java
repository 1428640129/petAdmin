package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathReview;

/**
 * 评价评论 数据层
 * 
 * @author Pet
 */
public interface PetBathReviewMapper
{
    /**
     * 查询评价评论信息
     * 
     * @param reviewId 评价ID
     * @return 评价评论信息
     */
    public PetBathReview selectBathReviewById(Long reviewId);

    /**
     * 查询评价评论列表
     * 
     * @param review 评价评论信息
     * @return 评价评论集合
     */
    public List<PetBathReview> selectBathReviewList(PetBathReview review);

    /**
     * 新增评价评论
     * 
     * @param review 评价评论信息
     * @return 结果
     */
    public int insertBathReview(PetBathReview review);

    /**
     * 修改评价评论
     * 
     * @param review 评价评论信息
     * @return 结果
     */
    public int updateBathReview(PetBathReview review);

    /**
     * 删除评价评论
     * 
     * @param reviewId 评价ID
     * @return 结果
     */
    public int deleteBathReviewById(Long reviewId);

    /**
     * 批量删除评价评论
     * 
     * @param reviewIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathReviewByIds(Long[] reviewIds);
}

