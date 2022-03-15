package com.crazymaker.springcloud.common.page;

import lombok.Data;

@Data
public class PageReq
{

    private int curPage = 1;

    private int pageSize = 20;

    public int getJpaPage()
    {
        return curPage - 1;
    }
}
