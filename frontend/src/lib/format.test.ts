import { describe, it, expect } from 'vitest'
import { money } from './format'

describe('money formatter', () => {
  it('formats a round number as VND currency', () => {
    const result = money.format(100000)
    // Vietnamese locale: "100.000 ₫" (dot-separated thousands, ₫ suffix)
    expect(result).toMatch(/100[.,]000/)
    expect(result).toContain('₫')
  })

  it('formats zero as 0 ₫', () => {
    const result = money.format(0)
    expect(result).toMatch(/0/)
    expect(result).toContain('₫')
  })

  it('formats a large number correctly', () => {
    const result = money.format(1500000)
    expect(result).toMatch(/1[.,]500[.,]000/)
    expect(result).toContain('₫')
  })

  it('formats a decimal price', () => {
    const result = money.format(99000.5)
    // Should contain digits around 99,000 range
    expect(result).toMatch(/99/)
    expect(result).toContain('₫')
  })

  it('is an Intl.NumberFormat instance', () => {
    expect(money).toBeInstanceOf(Intl.NumberFormat)
  })
})
