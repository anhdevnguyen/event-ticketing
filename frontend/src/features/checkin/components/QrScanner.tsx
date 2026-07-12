import { useEffect, useId } from 'react'
import { Html5QrcodeScanner } from 'html5-qrcode'

interface QrScannerProps {
  onScan: (qrCode: string) => void
}

export function QrScanner({ onScan }: QrScannerProps) {
  const id = useId().replaceAll(':', '')

  useEffect(() => {
    const scanner = new Html5QrcodeScanner(id, { fps: 10, qrbox: { width: 240, height: 240 } }, false)
    scanner.render(
      (text) => onScan(text),
      () => undefined,
    )
    return () => {
      void scanner.clear()
    }
  }, [id, onScan])

  return <div className="scanner-box" id={id} />
}
