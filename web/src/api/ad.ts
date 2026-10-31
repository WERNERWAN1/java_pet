import {get, post} from '/@/utils/http/axios';

enum URL {
    userList = '/api/ad/list',
    validList = '/api/ad/listValid',
    create = '/api/ad/create',
    update = '/api/ad/update',
    delete = '/api/ad/delete',
}

const listApi = async (params: any) =>
    get<any>({url: URL.userList, params: params, data: {}, headers: {}});
// 前台展示：仅拉取未过失效时间的广告
const listValidApi = async (params: any) =>
    get<any>({url: URL.validList, params: params, data: {}, headers: {}});
const createApi = async (data: any) =>
    post<any>({
        url: URL.create,
        params: {},
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });
const updateApi = async (data: any) =>
    post<any>({
        url: URL.update,
        data: data,
        headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
    });
const deleteApi = async (params: any) =>
    post<any>({url: URL.delete, params: params, headers: {}});

export {listApi, listValidApi, createApi, updateApi, deleteApi};
