package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathReview;

/**
 * 评价评论Service接口
 * 
 * @author Pet
 */
public interface IPetBathReviewService
{
    /**
     * 查询评价评论
     * 
     * @param reviewId 评价主键
     * @return 评价评论
     */
    public PetBathReview selectBathReviewByReviewId(Long reviewId);

    /**
     * 查询评价评论列表
     * 
     * @param review 评价评论
     * @return 评价评论集合
     */
    public List<PetBathReview> selectBathReviewList(PetBathReview review);

    /**
     * 新增评价评论
     * 
     * @param review 评价评论
     * @return 结果
     */
    public int insertBathReview(PetBathReview review);

    /**
     * 修改评价评论
     * 
     * @param review 评价评论
     * @return 结果
     */
    public int updateBathReview(PetBathReview review);

    /**
     * 批量删除评价评论
     * 
     * @param reviewIds 需要删除的评价主键集合
     * @return 结果
     */
    public int deleteBathReviewByReviewIds(Long[] reviewIds);

    /**
     * 删除评价评论信息
     * 
     * @param reviewId 评价主键
     * @return 结果
     */
    public int deleteBathReviewByReviewId(Long reviewId);

    /**
     * 回复评价
     * 
     * @param reviewId 评价ID
     * @param replyContent 回复内容
     * @return 结果
     */
    public int replyReview(Long reviewId, String replyContent);
}

