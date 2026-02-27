package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.common.utils.DateUtils;
import com.pet.common.utils.SecurityUtils;
import com.pet.business.mapper.PetBathReviewMapper;
import com.pet.system.domain.PetBathReview;
import com.pet.business.service.IPetBathReviewService;

/**
 * 评价评论Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathReviewServiceImpl implements IPetBathReviewService
{
    @Autowired
    private PetBathReviewMapper bathReviewMapper;

    /**
     * 查询评价评论
     * 
     * @param reviewId 评价主键
     * @return 评价评论
     */
    @Override
    public PetBathReview selectBathReviewByReviewId(Long reviewId)
    {
        return bathReviewMapper.selectBathReviewById(reviewId);
    }

    /**
     * 查询评价评论列表
     * 
     * @param review 评价评论
     * @return 评价评论
     */
    @Override
    public List<PetBathReview> selectBathReviewList(PetBathReview review)
    {
        return bathReviewMapper.selectBathReviewList(review);
    }

    /**
     * 新增评价评论
     * 
     * @param review 评价评论
     * @return 结果
     */
    @Override
    public int insertBathReview(PetBathReview review)
    {
        review.setStatus("0");
        return bathReviewMapper.insertBathReview(review);
    }

    /**
     * 修改评价评论
     * 
     * @param review 评价评论
     * @return 结果
     */
    @Override
    public int updateBathReview(PetBathReview review)
    {
        return bathReviewMapper.updateBathReview(review);
    }

    /**
     * 批量删除评价评论
     * 
     * @param reviewIds 需要删除的评价主键
     * @return 结果
     */
    @Override
    public int deleteBathReviewByReviewIds(Long[] reviewIds)
    {
        return bathReviewMapper.deleteBathReviewByIds(reviewIds);
    }

    /**
     * 删除评价评论信息
     * 
     * @param reviewId 评价主键
     * @return 结果
     */
    @Override
    public int deleteBathReviewByReviewId(Long reviewId)
    {
        return bathReviewMapper.deleteBathReviewById(reviewId);
    }

    /**
     * 回复评价
     */
    @Override
    public int replyReview(Long reviewId, String replyContent)
    {
        PetBathReview review = new PetBathReview();
        review.setReviewId(reviewId);
        review.setReplyContent(replyContent);
        review.setReplyTime(DateUtils.getNowDate());
        return bathReviewMapper.updateBathReview(review);
    }
}










