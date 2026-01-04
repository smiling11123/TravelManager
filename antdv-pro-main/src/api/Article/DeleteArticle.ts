import { request } from "../request";

export const deleteMyArticle = async () => {
    return request({
        url: '/article/auth/delete',

    })
}