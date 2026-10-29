<template>
  <div>
    <div class="page-view">
      <h3 style="margin-bottom: 16px;">我的订单</h3>
      <a-table
          size="middle"
          rowKey="id"
          :loading="loading"
          :columns="columns"
          :data-source="list"
          :pagination="{ size: 'default', pageSize: 10, showTotal: (total) => `共${total}条数据` }"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(text)">{{ getStatusText(text) }}</a-tag>
          </template>
          <template v-if="column.key === 'price'">
            <span style="color: #ff4d4f; font-weight: bold;">¥{{ text }}</span>
          </template>
          <template v-if="column.key === 'operation'">
            <a-popconfirm v-if="Number(record.status) === 1" title="确认收货?" ok-text="是" cancel-text="否" @confirm="handleConfirm(record)">
              <a>收货</a>
            </a-popconfirm>
            <a-popconfirm v-if="Number(record.status) === 1" title="确定取消?" ok-text="是" cancel-text="否" @confirm="handleCancel(record)">
              <a style="color: #ff4d4f; margin-left: 8px;">取消</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup>
import {userOrderListApi, updateApi, cancelUserOrderApi} from '/@/api/order';
import {message} from 'ant-design-vue';

const loading = ref(false);
const list = ref([]);
const userId = localStorage.getItem('user_id');

const columns = reactive([
  {title: '订单编号', dataIndex: 'order_number', key: 'order_number', align: 'center'},
  {title: '商品', dataIndex: 'title', key: 'title', align: 'center'},
  {title: '数量', dataIndex: 'count', key: 'count', align: 'center'},
  {title: '金额', dataIndex: 'price', key: 'price', align: 'center'},
  {title: '下单时间', dataIndex: 'order_time', key: 'order_time', align: 'center'},
  {title: '状态', dataIndex: 'status', key: 'status', align: 'center'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', width: 140},
]);

const getStatusText = (s) => ({'1': '待发货', '2': '已发货', '3': '已完成', '7': '已取消'}[s] || '未知');
const getStatusColor = (s) => ({'1': 'orange', '2': 'blue', '3': 'green', '7': 'red'}[s] || 'default');

const getDataList = () => {
  loading.value = true;
  userOrderListApi({userId}).then((res) => {
    loading.value = false;
    list.value = res.data || [];
  }).catch(() => { loading.value = false; });
};

const handleConfirm = (record) => {
  updateApi({id: record.id, status: '3'}).then(() => {
    message.success('收货成功');
    getDataList();
  }).catch(() => { message.error('操作失败'); });
};

const handleCancel = (record) => {
  cancelUserOrderApi({id: record.id}).then(() => {
    message.success('取消成功');
    getDataList();
  }).catch(() => { message.error('操作失败'); });
};

onMounted(() => { getDataList(); });
</script>

<style scoped lang="less">
.page-view { background: #fff; padding: 24px; min-height: 400px; }
</style>