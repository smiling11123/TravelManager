import { request } from "../request"

export interface LoginParams {
  username: string
  password: string
  type?: 'account'
}

export interface LoginMobileParams {
  mobile: string
  code: string
  type: 'mobile'
}

export interface LoginResultModel {
  token: string
}

export function loginApi(params: LoginParams) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: params,
  })
}

export function registerApi(params: LoginParams, isAuth: string) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: params,
    params: {isAuth: isAuth},
  })
}

export function logoutApi() {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}
