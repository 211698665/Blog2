package cn.henu.dao;

import cn.henu.pojo.Notice;

import java.util.List;

public interface NoticeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notice
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer noticeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notice
     *
     * @mbg.generated
     */
    int insert(Notice record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notice
     *
     * @mbg.generated
     */
    Notice selectByPrimaryKey(Integer noticeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notice
     *
     * @mbg.generated
     */
    List<Notice> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notice
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Notice record);
}