import { ref, watch, type Ref } from 'vue'

interface CountUpOptions {
  /** 动画时长（ms），默认 900 */
  duration?: number
  /** 保留小数位，默认 0 */
  decimals?: number
}

/**
 * 数字滚动动画：source 变化时从旧值 easeOutCubic 滚动到新值。
 * 返回格式化后的字符串 ref，可直接插值渲染。
 */
export function useCountUp(source: Ref<number>, options: CountUpOptions = {}) {
  const { duration = 900, decimals = 0 } = options
  const display = ref((0).toFixed(decimals))
  let rafId = 0

  const reducedMotion =
    typeof window !== 'undefined' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches

  watch(
    source,
    (to, from) => {
      cancelAnimationFrame(rafId)
      const target = Number(to) || 0
      const start = Number(from) || 0
      if (reducedMotion || duration <= 0 || target === start) {
        display.value = target.toFixed(decimals)
        return
      }
      const begin = performance.now()
      const tick = (now: number) => {
        const progress = Math.min((now - begin) / duration, 1)
        const eased = 1 - Math.pow(1 - progress, 3)
        display.value = (start + (target - start) * eased).toFixed(decimals)
        if (progress < 1) rafId = requestAnimationFrame(tick)
      }
      rafId = requestAnimationFrame(tick)
    },
    { immediate: true }
  )

  return display
}
