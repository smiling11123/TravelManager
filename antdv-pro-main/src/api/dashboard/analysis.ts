import { request } from "../request";

export const getArticleList = async () => {
    return request({
      url: '/article/list',
    })
}