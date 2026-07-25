import type { Directive } from 'vue'

/**
 * v-lazy 图片懒加载 + 渐入指令
 *
 * 用法一（原生 img，进入视口才加载）：
 *   <img v-lazy="realSrc" alt="" />
 *
 * 用法二（el-image / el-avatar 容器，仅渐入，懒加载由组件自身 lazy 属性完成）：
 *   <el-image v-lazy lazy :src="..." />
 *
 * 加载完成后为元素追加 .lazy-loaded，配合 style.css 中 .lazy-fade 实现淡入。
 */

type LazyEl = HTMLElement & { dataset: DOMStringMap & { lazySrc?: string } }

const revealImg = (img: HTMLImageElement) => {
  const done = () => img.classList.add('lazy-loaded')
  img.addEventListener('load', done, { once: true })
  img.addEventListener('error', done, { once: true })
  const src = img.dataset.lazySrc
  if (src) img.src = src
}

const observer =
  typeof IntersectionObserver !== 'undefined'
    ? new IntersectionObserver(
        entries => {
          for (const entry of entries) {
            if (!entry.isIntersecting) continue
            const img = entry.target as HTMLImageElement
            observer?.unobserve(img)
            revealImg(img)
          }
        },
        { rootMargin: '140px 0px' }
      )
    : null

const fadeInContainer = (el: HTMLElement) => {
  const attach = (img: HTMLImageElement | null) => {
    if (!img) {
      el.classList.add('lazy-loaded')
      return
    }
    if (img.complete && img.naturalWidth > 0) {
      el.classList.add('lazy-loaded')
      return
    }
    const done = () => el.classList.add('lazy-loaded')
    img.addEventListener('load', done, { once: true })
    img.addEventListener('error', done, { once: true })
  }
  requestAnimationFrame(() => attach(el.querySelector('img')))
}

export const vLazy: Directive<LazyEl, string | undefined> = {
  mounted(el, binding) {
    el.classList.add('lazy-fade')
    if (el.tagName === 'IMG') {
      const img = el as unknown as HTMLImageElement
      if (binding.value) {
        img.dataset.lazySrc = binding.value
        if (observer) observer.observe(img)
        else revealImg(img)
      } else if (img.complete) {
        img.classList.add('lazy-loaded')
      } else {
        const done = () => img.classList.add('lazy-loaded')
        img.addEventListener('load', done, { once: true })
        img.addEventListener('error', done, { once: true })
      }
      return
    }
    fadeInContainer(el)
  },
  updated(el, binding) {
    if (el.tagName !== 'IMG' || !binding.value || binding.value === binding.oldValue) return
    const img = el as unknown as HTMLImageElement
    img.classList.remove('lazy-loaded')
    img.dataset.lazySrc = binding.value
    if (observer) observer.observe(img)
    else revealImg(img)
  },
  unmounted(el) {
    observer?.unobserve(el)
  }
}
