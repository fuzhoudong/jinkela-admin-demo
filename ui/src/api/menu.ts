import service from "@/api/service";

// 菜单列表
export function menuLists() {
  return service({ url: '/api/jinkela/menus', method: 'GET' })
}

// 添加菜单
export function menuAdd(params: any) {
  return service({ url: '/api/system/menu/add', method: 'POST', params })
}

// 编辑菜单
export function menuEdit(params: any) {
  return service({ url: `/api/jinkela/menus/${params.id}`, method: 'PUT', data: params })
}

// 菜单删除
export function menuDelete(params: any) {
  return service({ url: '/api/system/menu/del', method: 'POST', params })
}

// 菜单删除
export function menuDetail(params: any) {
  return service({ url: `/api/jinkela/menus/${params.id}`, method: 'GET', params })
}
