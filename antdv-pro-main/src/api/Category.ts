import { request } from "./request";

export const getHotCategoryList = async ()=>{
    return request({
        url: '/category/hotList',
    })
}