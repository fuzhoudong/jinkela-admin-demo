<script lang="ts" setup>
import { roleDelete, roleLists } from "@/api/role";
import Pagination from "@/components/Pagination.vue";
import { usePaging } from "@/hooks/usePaging";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { nextTick, ref, shallowRef } from "vue";
import { useRouter } from "vue-router";
import RoleEdit from "./RoleEdit.vue";

const editRef = shallowRef();
const showEdit = ref(false);
const router = useRouter();

const { pager, getLists } = usePaging({
  fetchFun: roleLists,
});

const handleAdd = async () => {
  showEdit.value = true;
  await nextTick();
  editRef.value?.open("add");
};

const handleEdit = async (data) => {
  showEdit.value = true;
  await nextTick();
  editRef.value?.open("edit");
  editRef.value?.setFormData(data);
};

const handleAuth = () => {
  // showAuth.value = true
  // await nextTick()
  // authRef.value?.open()
  // authRef.value?.setFormData(data)
  console.log(router.getRoutes());
  router.push({ name: "RoleAuthorize", params: { id: 1 } });
};

// 删除角色
const handleDelete = async (row) => {
  await ElMessageBox.confirm("确定要删除？", "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  });
  await roleDelete({ id: row.id, version: row.version });
  ElMessage.success("删除成功");
  getLists();
};

getLists();
</script>

<template>
  <div class="role-lists">
    <el-card class="!border-none" shadow="never">
      <div>
        <el-button
          v-perms="['juesexinzeng']"
          type="primary"
          @click="handleAdd"
          :icon="Plus"
        >
          新增
        </el-button>
      </div>
      <div class="mt-4">
        <div>
          <el-table :data="pager.lists" size="large" v-loading="pager.loading">
            <el-table-column prop="id" label="ID" min-width="100" />
            <el-table-column prop="name" label="名称" min-width="150" />
            <el-table-column
              prop="remark"
              label="备注"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column prop="enabled" label="可用状态" min-width="100">
              <template #default="{ row }">
                <el-tag v-if="row.enabled">正常</el-tag>
                <el-tag v-else type="danger">停用</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="createdAt"
              label="创建时间"
              min-width="180"
            />
            <el-table-column label="操作" width="290" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-perms="['juesegengxin']"
                  link
                  type="primary"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button link type="primary" @click="handleAuth()">
                  权限设置
                </el-button>
                <el-button
                  v-perms="['jueseshanchu']"
                  link
                  type="danger"
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="flex justify-end mt-4">
          <Pagination v-model="pager" @change="getLists" />
        </div>
      </div>
    </el-card>
    <RoleEdit
      v-if="showEdit"
      ref="editRef"
      @success="getLists"
      @close="showEdit = false"
    />
  </div>
</template>
