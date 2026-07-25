export const HEADER_INDICATOR_CHANGED = 'header-indicator-changed'

export const refreshHeaderIndicators = () => {
  window.dispatchEvent(new Event(HEADER_INDICATOR_CHANGED))
}
