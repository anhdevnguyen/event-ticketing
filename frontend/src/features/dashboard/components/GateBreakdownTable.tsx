import type { GateStats } from '../types'

interface Props {
  gates: GateStats[]
}

export function GateBreakdownTable({ gates }: Props) {
  return (
    <section className="dashboard-section">
      <h2>Gate breakdown</h2>
      <div className="gate-table">
        <div className="gate-row gate-head">
          <span>Gate</span>
          <span>Checked in</span>
        </div>
        {gates.map((gate) => (
          <div className="gate-row" key={gate.gateId}>
            <span>{gate.gateName}</span>
            <strong>{gate.checkedIn.toLocaleString()}</strong>
          </div>
        ))}
      </div>
    </section>
  )
}
