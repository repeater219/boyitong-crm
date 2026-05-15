// 博易通 Logo 选型
// 切换方式：把下面这行 export default 后的 V1/V2/V3/V4/V5 换一个

export default LogoV4

export { LogoV1, LogoV2, LogoV3, LogoV4, LogoV5 }

/* ────────────── 方案一：经典科技圆标（当前） ──────────────
   渐变方块 + 抽象 B 字母 + 金色点缀
   风格：现代、稳重、企业级
*/
function LogoV1({ collapsed = false, size = 32 }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: collapsed ? 0 : 10 }}>
      <svg width={size} height={size} viewBox="0 0 32 32" fill="none">
        <rect x="0.5" y="0.5" width="31" height="31" rx="8" fill="url(#v1)" />
        <rect x="9" y="8" width="3" height="16" rx="1.5" fill="white" />
        <path d="M12 12.5C12 11 13.5 10 15.5 10C18 10 19 10.5 19 12.5C19 14.5 17.5 15 16 15C14.5 15 12 14.5 12 18C12 20.5 14 22 16.5 22C18.5 22 20 21 20 19.5" stroke="white" strokeWidth="2.2" strokeLinecap="round" fill="none" />
        <circle cx="25" cy="7" r="2" fill="#FFD700" />
        <defs><linearGradient id="v1" x1="0" y1="0" x2="32" y2="32"><stop stopColor="#6366F1"/><stop offset="1" stopColor="#A78BFA"/></linearGradient></defs>
      </svg>
      {!collapsed && <LogoText />}
    </div>
  )
}

/* ────────────── 方案二：无限环/莫比乌斯带 ──────────────
   象征"博易通"的畅通无阻、循环不息
   风格：简洁、流畅、互联网感
*/
function LogoV2({ collapsed = false, size = 32 }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: collapsed ? 0 : 10 }}>
      <svg width={size} height={size} viewBox="0 0 32 32" fill="none">
        {/* 无限符号 ∞ — 代表通达无限 */}
        <path d="M8 16C8 12 10 10 13 10C16 10 16 13 16 16C16 19 16 22 19 22C22 22 24 20 24 16" stroke="url(#v2)" strokeWidth="2.5" strokeLinecap="round" fill="none" />
        <path d="M8 16C8 20 10 22 13 22C16 22 16 19 16 16C16 13 16 10 19 10C22 10 24 12 24 16" stroke="url(#v2)" strokeWidth="2.5" strokeLinecap="round" fill="none" opacity="0.4" />
        {/* 中心圆点 — 连接点 */}
        <circle cx="16" cy="16" r="1.5" fill="#FFD700" />
        <defs><linearGradient id="v2" x1="8" y1="10" x2="24" y2="22"><stop stopColor="#6366F1"/><stop offset="1" stopColor="#06B6D4"/></linearGradient></defs>
      </svg>
      {!collapsed && <LogoText />}
    </div>
  )
}

/* ────────────── 方案三：钻石/六边形 ──────────────
   象征"博易"— 广博的交易网络，稳固、珍贵
   风格：高端、金融、企业级
*/
function LogoV3({ collapsed = false, size = 32 }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: collapsed ? 0 : 10 }}>
      <svg width={size} height={size} viewBox="0 0 32 32" fill="none">
        {/* 六边形框架 */}
        <polygon points="16,2 28,9 28,23 16,30 4,23 4,9" fill="url(#v3)" opacity="0.15" stroke="url(#v3)" strokeWidth="1.5" />
        {/* 内部钻石 — 交易核心 */}
        <polygon points="16,8 22,12 16,24 10,12" fill="white" opacity="0.9" />
        <polygon points="16,8 22,12 16,16 10,12" fill="url(#v3)" opacity="0.6" />
        {/* 顶部光点 */}
        <circle cx="16" cy="7" r="1.5" fill="#FFD700" />
        <defs><linearGradient id="v3" x1="4" y1="2" x2="28" y2="30"><stop stopColor="#6366F1"/><stop offset="1" stopColor="#EC4899"/></linearGradient></defs>
      </svg>
      {!collapsed && <LogoText />}
    </div>
  )
}

/* ────────────── 方案四：波浪/通达 ──────────────
   波浪代表"博易通"—— 广博如海、交易通达、流通不息
   风格：流畅、大气、现代
*/
function LogoV4({ collapsed = false, size = 32 }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: collapsed ? 0 : 10 }}>
      <svg width={size} height={size} viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        {/* 圆形背景 */}
        <circle cx="16" cy="16" r="15" fill="url(#v4)" opacity="0.1" stroke="url(#v4)" strokeWidth="1.5" />
        {/* 主波浪 — 三道递进波浪线 */}
        <path d="M4 18C8 12 12 22 16 18C20 14 24 24 28 18" stroke="url(#v4)" strokeWidth="2.5" strokeLinecap="round" fill="none" />
        <path d="M4 22C8 16 12 26 16 22C20 18 24 28 28 22" stroke="url(#v4)" strokeWidth="1.8" strokeLinecap="round" fill="none" opacity="0.5" />
        <path d="M4 26C8 20 12 30 16 26C20 22 24 32 28 26" stroke="url(#v4)" strokeWidth="1" strokeLinecap="round" fill="none" opacity="0.25" />
        {/* 浪尖光点 — 交易节点 */}
        <circle cx="16" cy="18" r="2" fill="white" />
        <defs><linearGradient id="v4" x1="4" y1="12" x2="28" y2="28"><stop stopColor="#6366F1"/><stop offset="1" stopColor="#06B6D4"/></linearGradient></defs>
      </svg>
      {!collapsed && <LogoText />}
    </div>
  )
}

/* ────────────── 方案五：折纸/飘带 B ──────────────
   折纸风格的 B 字母，立体动感
   风格：年轻、创意、互联网
*/
function LogoV5({ collapsed = false, size = 32 }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: collapsed ? 0 : 10 }}>
      <svg width={size} height={size} viewBox="0 0 32 32" fill="none">
        <rect x="0.5" y="0.5" width="31" height="31" rx="10" fill="#0F0F23" stroke="url(#v5)" strokeWidth="1" />
        {/* 折纸 B — 三个彩色三角面 */}
        <polygon points="8,6 14,6 14,16 8,16" fill="#6366F1" />
        <polygon points="14,6 20,12 14,16" fill="#818CF8" />
        <polygon points="8,16 14,16 14,26 8,26" fill="#4F46E5" />
        <polygon points="14,16 20,20 14,26" fill="#A78BFA" />
        {/* 高光线 */}
        <path d="M8 6L14 6L20 12" stroke="white" strokeWidth="0.5" opacity="0.3" fill="none" />
        <defs><linearGradient id="v5" x1="8" y1="6" x2="20" y2="26"><stop stopColor="#6366F1"/><stop offset="1" stopColor="#A78BFA"/></linearGradient></defs>
      </svg>
      {!collapsed && <LogoText />}
    </div>
  )
}

/* ────────────── 文字部分 ────────────── */
function LogoText() {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', lineHeight: 1.2 }}>
      <span style={{ fontSize: 16, fontWeight: 700, color: '#fff' }}>博易通</span>
      <span style={{ fontSize: 10, color: 'rgba(255,255,255,0.6)', letterSpacing: 1 }}>CRM</span>
    </div>
  )
}