import { describe, it, expect, beforeEach, vi } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { navigate, usePath } from './navigation'

beforeEach(() => {
  // Reset location to '/'
  window.history.pushState({}, '', '/')
})

describe('navigate()', () => {
  it('changes window.location.pathname', () => {
    navigate('/events')
    expect(window.location.pathname).toBe('/events')
  })

  it('dispatches a popstate event', () => {
    const listener = vi.fn()
    window.addEventListener('popstate', listener)
    navigate('/tickets')
    expect(listener).toHaveBeenCalledOnce()
    window.removeEventListener('popstate', listener)
  })

  it('navigates to nested paths', () => {
    navigate('/events/42')
    expect(window.location.pathname).toBe('/events/42')
  })

  it('navigates to root path', () => {
    navigate('/events')
    navigate('/')
    expect(window.location.pathname).toBe('/')
  })
})

describe('usePath()', () => {
  it('returns the current pathname on mount', () => {
    window.history.pushState({}, '', '/events')
    const { result } = renderHook(() => usePath())
    expect(result.current).toBe('/events')
  })

  it('updates when navigate() is called', () => {
    window.history.pushState({}, '', '/events')
    const { result } = renderHook(() => usePath())

    act(() => {
      navigate('/tickets')
    })

    expect(result.current).toBe('/tickets')
  })

  it('updates on manual pushState + popstate event', () => {
    const { result } = renderHook(() => usePath())

    act(() => {
      window.history.pushState({}, '', '/checkin')
      window.dispatchEvent(new PopStateEvent('popstate'))
    })

    expect(result.current).toBe('/checkin')
  })

  it('cleans up popstate listener on unmount', () => {
    const { result, unmount } = renderHook(() => usePath())

    act(() => { navigate('/events') })
    expect(result.current).toBe('/events')

    unmount()

    // After unmount, hook should no longer track changes
    act(() => { navigate('/tickets') })
    expect(result.current).toBe('/events') // still the last value before unmount
  })
})
