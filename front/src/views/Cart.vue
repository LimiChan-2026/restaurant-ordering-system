<template>
  <div class="cart-page">
    <div class="page-heading">
      <h1>购物车</h1>
      <el-button text @click="loadData">刷新</el-button>
    </div>

    <SkeletonScreen v-if="loading" variant="list" :count="3" />
    <el-empty v-else-if="cartItems.length === 0" description="购物车暂无商品">
      <el-button type="primary" @click="router.push('/dishes')">去浏览菜品</el-button>
    </el-empty>

    <template v-else>
      <el-card v-for="item in cartItems" :key="item.id" class="cart-item">
        <div class="item-content">
          <el-checkbox :model-value="item.isSelected" @change="toggleSelected(item, Boolean($event))" />
          <el-image v-lazy lazy :src="item.dishesCover" fit="cover" class="cover">
            <template #error><div class="image-placeholder">暂无图片</div></template>
          </el-image>
          <div class="item-info">
            <strong>{{ item.dishesName }}</strong>
            <span>{{ item.dishesPackageName }}</span>
            <span class="price">¥{{ formatMoney(item.dishesPackagePrice) }}</span>
          </div>
          <el-input-number
            :model-value="item.plusNumber"
            :min="1"
            :max="99"
            @change="updateQuantity(item, $event)"
          />
          <strong class="subtotal">¥{{ formatMoney(item.dishesPackagePrice * item.plusNumber) }}</strong>
          <el-button type="danger" link @click="removeItem(item)">删除</el-button>
        </div>
      </el-card>

      <div class="settlement-bar">
        <el-checkbox :model-value="allSelected" :indeterminate="isIndeterminate" @change="toggleAll(Boolean($event))">
          全选
        </el-checkbox>
        <span>已选 {{ selectedItems.length }} 件，合计：<strong>¥{{ formatMoney(totalPrice) }}</strong></span>
        <el-button type="primary" :disabled="selectedItems.length === 0" @click="checkout">去结算</el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteCartItem, getCartItems, updateCartItem } from '@/api/cart'
import type { CartItem } from '@/types/cart'
import SkeletonScreen from '@/components/SkeletonScreen.vue'

const router = useRouter()
const loading = ref(false)
const cartItems = ref<CartItem[]>([])

const selectedItems = computed(() => cartItems.value.filter(item => item.isSelected))
const allSelected = computed(() => cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length)
const isIndeterminate = computed(() => selectedItems.value.length > 0 && !allSelected.value)
const totalPrice = computed(() => selectedItems.value.reduce((sum, item) => sum + item.dishesPackagePrice * item.plusNumber, 0))
const formatMoney = (value: number) => Number(value || 0).toFixed(2)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCartItems()
    cartItems.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.message || '查询购物车失败')
  } finally {
    loading.value = false
  }
}

const saveItem = async (item: CartItem, patch: Partial<Pick<CartItem, 'plusNumber' | 'isSelected'>>) => {
  await updateCartItem({
    id: item.id,
    dishesPackageId: item.dishesPackageId,
    plusNumber: patch.plusNumber ?? item.plusNumber,
    isSelected: patch.isSelected ?? item.isSelected
  })
}

const toggleSelected = async (item: CartItem, isSelected: boolean) => {
  try {
    await saveItem(item, { isSelected })
    item.isSelected = isSelected
  } catch (error: any) {
    ElMessage.error(error.message || '修改选中状态失败')
  }
}

const updateQuantity = async (item: CartItem, value: number | undefined) => {
  const plusNumber = Number(value || 1)
  try {
    await saveItem(item, { plusNumber })
    item.plusNumber = plusNumber
  } catch (error: any) {
    ElMessage.error(error.message || '修改数量失败')
    loadData()
  }
}

const toggleAll = async (isSelected: boolean) => {
  try {
    await Promise.all(cartItems.value
      .filter(item => item.isSelected !== isSelected)
      .map(item => saveItem(item, { isSelected })))
    cartItems.value.forEach(item => { item.isSelected = isSelected })
  } catch (error: any) {
    ElMessage.error(error.message || '全选操作失败')
    loadData()
  }
}

const removeItem = async (item: CartItem) => {
  try {
    await ElMessageBox.confirm(`确认删除“${item.dishesName} - ${item.dishesPackageName}”吗？`, '删除商品', { type: 'warning' })
    await deleteCartItem(item.id)
    cartItems.value = cartItems.value.filter(current => current.id !== item.id)
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const checkout = () => router.push('/order-confirm')

onMounted(loadData)
</script>

<style scoped lang="scss">
.cart-page { max-width: 960px; margin: 0 auto; padding: 34px 20px 118px; }
.page-heading { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; h1 { margin: 0; color: #30241f; font-size: 28px; font-weight: 750; } }
.cart-item { margin-bottom: 14px; border-radius: 16px; transition: transform .2s ease, box-shadow .2s ease; &:hover { transform: translateY(-2px); box-shadow: 0 12px 26px rgba(65,42,28,.09); } }
.item-content { display: flex; align-items: center; gap: 18px; }
.cover { width: 82px; height: 82px; flex: none; border: 1px solid #f0e3db; border-radius: 12px; }
.image-placeholder { display: grid; width: 100%; height: 100%; place-items: center; color: #9a7869; background: #fff0e8; font-size: 12px; }
.item-info { display: flex; flex: 1; flex-direction: column; gap: 7px; color: #796f68; }
.item-info strong { color: #352923; font-size: 16px; }
.price, .subtotal, .settlement-bar strong { color: #c9502d; font-variant-numeric: tabular-nums; }
.subtotal { min-width: 86px; color: #c9502d; font-weight: 750; text-align: right; }
.settlement-bar { position: fixed; right: max(16px, calc((100% - 960px) / 2)); bottom: 18px; left: max(16px, calc((100% - 960px) / 2)); z-index: 10; display: flex; align-items: center; justify-content: flex-end; gap: 28px; padding: 15px 20px; border: 1px solid rgba(238, 218, 207, .95); border-radius: 16px; background: rgba(255,253,251,.94); box-shadow: 0 12px 32px rgba(65,42,28,.14); backdrop-filter: blur(14px); }
@media (max-width: 700px) { .cart-page { padding: 22px 14px 132px; } .page-heading h1 { font-size: 24px; } .item-content { flex-wrap: wrap; gap: 14px; } .item-info { min-width: 150px; } .subtotal { text-align: left; } .settlement-bar { right: 12px; bottom: 12px; left: 12px; flex-wrap: wrap; justify-content: space-between; gap: 12px; padding: 13px 16px; } }
</style>
