/* ============================================================
   삼척 분산 관광 — front logic (실 API fetch + 시드 폴백)
   ============================================================ */
'use strict';

const CONG = {
  LOW:    { label: '한적', cls: 'calm',  rank: 0, on: 1 },
  MEDIUM: { label: '보통', cls: 'busy',  rank: 1, on: 2 },
  HIGH:   { label: '붐빔', cls: 'crowd', rank: 2, on: 3 },
};
const CAT = { COAST: '해안', MOUNTAIN: '산·계곡', HISTORY: '역사', MINE_CULTURE: '폐광 문화', CAVE: '동굴' };

const ICONS = {
  COAST: '<path d="M2 13c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2"/><path d="M2 18c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2"/>',
  MOUNTAIN: '<path d="M3 20l6-11 4 7 2-3 6 7z"/>',
  HISTORY: '<path d="M4 9h16M5 9v9M19 9v9M9 9v9M15 9v9M3 20h18M12 3l8 6H4z"/>',
  MINE_CULTURE: '<path d="M12 3l9 9-9 9-9-9z"/>',
  CAVE: '<path d="M4 20v-8a8 8 0 0116 0v8M9 20v-4a3 3 0 016 0v4"/>',
};
const icon = (cat) =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">${ICONS[cat] || ICONS.MOUNTAIN}</svg>`;

const GEM_SVG =
  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"><path d="M12 3l2.6 5.6L21 9.3l-4.5 4.2 1.1 6.2L12 16.9 6.4 19.7l1.1-6.2L3 9.3l6.4-.7z"/></svg>';

const SEED = [
  { id: 1, name: '쏠비치 삼척', description: '대표 해안 리조트. 성수기마다 발길이 몰린다.', category: 'COAST', congestionLevel: 'HIGH', localGem: false, latitude: 37.46, longitude: 129.17, address: '강원 삼척시 수로부인길' },
  { id: 2, name: '장호항', description: '한국의 나폴리. 투명카약으로 붐비는 포구.', category: 'COAST', congestionLevel: 'HIGH', localGem: false, latitude: 37.30, longitude: 129.31, address: '강원 삼척시 근덕면' },
  { id: 3, name: '초곡용굴촛대바위길', description: '한적한 해안 비경을 잇는 산책로.', category: 'COAST', congestionLevel: 'LOW', localGem: true, latitude: 37.36, longitude: 129.26, address: '강원 삼척시 근덕면' },
  { id: 4, name: '덕봉산 해안생태탐방로', description: '숨은 해안 생태길. 사람보다 새가 많다.', category: 'COAST', congestionLevel: 'LOW', localGem: true, latitude: 37.33, longitude: 129.28, address: '강원 삼척시 근덕면' },
  { id: 5, name: '무건리 이끼폭포', description: '신비로운 산간 이끼폭포. 깊은 숲의 정적.', category: 'MOUNTAIN', congestionLevel: 'LOW', localGem: true, latitude: 37.20, longitude: 129.05, address: '강원 삼척시 도계읍' },
  { id: 6, name: '환선굴', description: '국내 최대 석회동굴. 적당한 발길.', category: 'CAVE', congestionLevel: 'MEDIUM', localGem: false, latitude: 37.27, longitude: 129.05, address: '강원 삼척시 신기면' },
  { id: 7, name: '죽서루', description: '관동팔경의 누각, 보물.', category: 'HISTORY', congestionLevel: 'MEDIUM', localGem: false, latitude: 37.44, longitude: 129.16, address: '강원 삼척시 죽서루길' },
  { id: 8, name: '도계 폐광촌 문화공간', description: '폐광지를 재생한 문화공간. 조용한 영감.', category: 'MINE_CULTURE', congestionLevel: 'LOW', localGem: true, latitude: 37.23, longitude: 129.06, address: '강원 삼척시 도계읍' },
];

const COURSES = [
  { themeLabel: '힐링', name: '한적한 해안 힐링', stops: [
    { n: '초곡용굴촛대바위길', c: '한적 해안 산책', gem: true },
    { n: '덕봉산 해안생태탐방로', c: '생태 탐방', gem: true },
    { n: '죽서루', c: '누각에서 쉼', gem: false } ] },
  { themeLabel: '체험', name: '폐광에서 동굴까지', stops: [
    { n: '도계 폐광촌 문화공간', c: '재생 문화', gem: true },
    { n: '환선굴', c: '석회동굴 탐험', gem: false },
    { n: '무건리 이끼폭포', c: '이끼숲 트레킹', gem: true } ] },
  { themeLabel: '친환경', name: '숲과 물의 분산길', stops: [
    { n: '무건리 이끼폭포', c: '이끼폭포', gem: true },
    { n: '초곡용굴촛대바위길', c: '해안 산책', gem: true },
    { n: '장호항', c: '비수기 방문 권장', gem: false } ] },
];

const state = { attractions: [], filter: 'all', apiLive: false };
const $ = (s) => document.querySelector(s);

/* -------------------- data load -------------------- */
async function loadAttractions() {
  try {
    const res = await fetch('/api/attractions', { headers: { Accept: 'application/json' } });
    if (!res.ok) throw new Error('http ' + res.status);
    const data = await res.json();
    if (!Array.isArray(data) || data.length === 0) throw new Error('empty');
    state.attractions = data;
    state.apiLive = true;
  } catch (e) {
    state.attractions = SEED.map((a) => ({ ...a }));
    state.apiLive = false;
  }
  setApiState();
}

function setApiState() {
  const el = $('#apiState');
  $('#apiText').textContent = state.apiLive ? '실시간 API 연결됨' : '데모 데이터 (API 미연결)';
  el.classList.toggle('live', state.apiLive);
}

/* -------------------- badges / helpers -------------------- */
function badge(level) {
  const m = CONG[level] || CONG.LOW;
  let bars = '';
  for (let i = 1; i <= 3; i++) bars += `<i class="${i <= m.on ? 'on' : ''}"></i>`;
  return `<span class="badge ${m.cls}"><span class="lvl">${bars}</span>${m.label}</span>`;
}
const gemTag = () => `<span class="gem">${GEM_SVG}로컬 명소</span>`;
const fix = (n) => (typeof n === 'number' ? n.toFixed(2) : '—');

/* -------------------- stats -------------------- */
function renderStats() {
  const a = state.attractions;
  const total = a.length || 1;
  const c = { HIGH: 0, MEDIUM: 0, LOW: 0 };
  a.forEach((x) => { c[x.congestionLevel] = (c[x.congestionLevel] || 0) + 1; });
  const gems = a.filter((x) => x.localGem).length;

  const bar = $('#distBar');
  bar.querySelector('.s-crowd').style.width = (c.HIGH / total * 100) + '%';
  bar.querySelector('.s-busy').style.width = (c.MEDIUM / total * 100) + '%';
  bar.querySelector('.s-calm').style.width = (c.LOW / total * 100) + '%';

  $('#distLegend').innerHTML = [
    ['crowd', '붐빔', c.HIGH], ['busy', '보통', c.MEDIUM], ['calm', '한적', c.LOW],
  ].map(([cls, lbl, n]) =>
    `<span class="item"><span class="swatch" style="background:var(--${cls})"></span>${lbl} <b>${n}</b></span>`).join('');

  animateCount($('#gemCount'), gems);
}

function animateCount(el, to) {
  let cur = 0; const step = Math.max(1, Math.ceil(to / 14));
  clearInterval(el._t);
  el._t = setInterval(() => {
    cur += step; if (cur >= to) { cur = to; clearInterval(el._t); }
    el.textContent = cur;
  }, 40);
}

/* -------------------- filters + cards -------------------- */
function renderFilters() {
  const defs = [
    ['all', '전체', null], ['LOW', '한적', 'calm'], ['MEDIUM', '보통', 'busy'],
    ['HIGH', '붐빔', 'crowd'], ['gem', '로컬 명소', null],
  ];
  $('#filters').innerHTML = defs.map(([f, lbl, cls]) =>
    `<button class="chip" data-filter="${f}" aria-pressed="${state.filter === f}">
      ${cls ? `<span class="cdot" style="background:var(--${cls})"></span>` : ''}${lbl}</button>`).join('');
  $('#filters').querySelectorAll('.chip').forEach((b) =>
    b.addEventListener('click', () => { state.filter = b.dataset.filter; renderFilters(); renderCards(); }));
}

function filtered() {
  const f = state.filter;
  if (f === 'all') return state.attractions;
  if (f === 'gem') return state.attractions.filter((a) => a.localGem);
  return state.attractions.filter((a) => a.congestionLevel === f);
}

function renderCards() {
  const list = filtered();
  $('#cards').innerHTML = list.map((a, i) => {
    const m = CONG[a.congestionLevel] || CONG.LOW;
    return `<article class="card" data-id="${a.id}"
        style="--accent:var(--${m.cls}); animation:rise .6s both; animation-delay:${i * 0.04}s">
      ${a.localGem ? gemTag() : ''}
      <div class="cat">${CAT[a.category] || a.category}</div>
      <div class="name">${a.name}</div>
      <div class="desc">${a.description || ''}</div>
      <div class="meta">${badge(a.congestionLevel)}<span class="coord">${fix(a.latitude)}, ${fix(a.longitude)}</span></div>
    </article>`;
  }).join('');
  $('#cards').querySelectorAll('.card').forEach((el) =>
    el.addEventListener('click', () => openDrawer(byId(+el.dataset.id))));
}

const byId = (id) => state.attractions.find((a) => a.id === id);

/* -------------------- courses -------------------- */
function renderCourses() {
  $('#rail').innerHTML = COURSES.map((c) => `
    <article class="route">
      <div class="theme">${c.themeLabel} 코스</div>
      <h3>${c.name}</h3>
      <ol>${c.stops.map((s) =>
        `<li class="${s.gem ? 'gemstop' : ''}">${s.n}<small>${s.c}</small></li>`).join('')}</ol>
    </article>`).join('');
}

/* -------------------- drawer -------------------- */
const drawer = $('#drawer');
const scrim = $('#scrim');

function openDrawer(att) {
  if (!att) return;
  drawer.innerHTML = drawerHTML(att);
  drawer.classList.add('open');
  scrim.classList.add('open');
  drawer.setAttribute('aria-hidden', 'false');
  bindDrawer(att);
  loadAlternatives(att);
}
function closeDrawer() {
  drawer.classList.remove('open');
  scrim.classList.remove('open');
  drawer.setAttribute('aria-hidden', 'true');
}
scrim.addEventListener('click', closeDrawer);
document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeDrawer(); });

function drawerHTML(a) {
  return `
    <div class="drawer-head">
      <div>
        <div class="cat">${CAT[a.category] || a.category}${a.localGem ? ' · 로컬 명소' : ''}</div>
        <h3>${a.name}</h3>
      </div>
      <button class="close" id="closeBtn" aria-label="닫기">✕</button>
    </div>
    <div class="drawer-body">
      <p class="desc">${a.description || ''}</p>
      <dl class="dl">
        <dt>혼잡도</dt><dd id="dHeadBadge">${badge(a.congestionLevel)}</dd>
        <dt>주소</dt><dd>${a.address || '—'}</dd>
        <dt>좌표</dt><dd>${fix(a.latitude)}, ${fix(a.longitude)}</dd>
      </dl>

      <div class="cong-ctl">
        <div class="lbl">실시간 혼잡도 갱신 (PATCH)</div>
        <div class="seg" id="congSeg">
          <button class="calm"  data-lv="LOW"    data-on="${a.congestionLevel === 'LOW' ? 1 : 0}">한적</button>
          <button class="busy"  data-lv="MEDIUM" data-on="${a.congestionLevel === 'MEDIUM' ? 1 : 0}">보통</button>
          <button class="crowd" data-lv="HIGH"   data-on="${a.congestionLevel === 'HIGH' ? 1 : 0}">붐빔</button>
        </div>
      </div>

      <div class="flow-title">
        <h4 class="serif">분산 추천</h4>
      </div>
      <p class="flow-note" id="flowNote">한산한 대안을 찾는 중…</p>
      <div class="flowviz" id="flowviz">
        <svg class="flow-svg" id="flowSvg"></svg>
        <div class="flow-nodes" id="flowNodes"></div>
      </div>
    </div>`;
}

function bindDrawer(att) {
  $('#closeBtn').addEventListener('click', closeDrawer);
  $('#congSeg').querySelectorAll('button').forEach((b) =>
    b.addEventListener('click', () => setCongestion(att, b.dataset.lv)));
}

/* -------------------- alternatives (분산 흐름) -------------------- */
async function loadAlternatives(att) {
  let alts = null;
  try {
    const res = await fetch(`/api/attractions/${att.id}/alternatives`, { headers: { Accept: 'application/json' } });
    if (res.ok) alts = await res.json();
  } catch (e) { /* fall through */ }
  if (!Array.isArray(alts)) alts = computeAlternatives(att);
  renderFlow(att, alts);
}

function computeAlternatives(base) {
  const baseRank = (CONG[base.congestionLevel] || CONG.LOW).rank;
  return state.attractions
    .filter((a) => a.category === base.category && a.id !== base.id)
    .filter((a) => (CONG[a.congestionLevel] || CONG.LOW).rank < baseRank || a.localGem)
    .sort((x, y) =>
      (Number(y.localGem) - Number(x.localGem)) ||
      ((CONG[x.congestionLevel] || CONG.LOW).rank - (CONG[y.congestionLevel] || CONG.LOW).rank) ||
      x.name.localeCompare(y.name))
    .slice(0, 3);
}

function nodeRow(a, base) {
  const m = CONG[a.congestionLevel] || CONG.LOW;
  const cls = base ? 'node-base' : 'alt';
  return `<div class="${cls}" ${base ? '' : `data-id="${a.id}"`}>
      <span class="ic">${icon(a.category)}</span>
      <span class="t"><b>${a.name}</b><small>${CAT[a.category] || a.category}${a.localGem ? ' · 로컬 명소' : ''}</small></span>
      <span class="pill ${m.cls}">${m.label}</span>
    </div>`;
}

function renderFlow(base, alts) {
  const note = $('#flowNote');
  const nodes = $('#flowNodes');
  if (!nodes) return;

  if (!alts || alts.length === 0) {
    note.textContent = base.congestionLevel === 'LOW'
      ? '이미 한적한 곳이에요. 마음껏 머무세요.'
      : '같은 결의 한적한 대안이 아직 없어요.';
    nodes.innerHTML = nodeRow(base, true) + `<div class="empty-alt">추천할 대안이 없습니다.</div>`;
    $('#flowSvg').innerHTML = '';
    return;
  }

  note.innerHTML = `<b>${base.name}</b>의 발걸음을 이곳으로 흩어보세요 — 같은 ${CAT[base.category] || ''}, 더 한적한 ${alts.length}곳.`;
  nodes.innerHTML = nodeRow(base, true) +
    `<div class="alts">${alts.map((a, i) =>
      nodeRow(a).replace('class="alt"', `class="alt" style="animation-delay:${0.12 + i * 0.1}s"`)).join('')}</div>`;

  nodes.querySelectorAll('.alt').forEach((el) =>
    el.addEventListener('click', () => { const a = byId(+el.dataset.id); if (a) openDrawer(a); }));

  setTimeout(drawConnectors, 80);
}

function drawConnectors() {
  const viz = $('#flowviz'); const svg = $('#flowSvg');
  if (!viz || !svg) return;
  const base = viz.querySelector('.node-base');
  const alts = [...viz.querySelectorAll('.alt')];
  if (!base || alts.length === 0) { svg.innerHTML = ''; return; }
  const r0 = viz.getBoundingClientRect();
  svg.setAttribute('width', viz.clientWidth);
  svg.setAttribute('height', viz.clientHeight);

  const b = base.getBoundingClientRect();
  const x1 = b.left + b.width / 2 - r0.left;
  const y1 = b.bottom - r0.top;

  svg.innerHTML = alts.map((el) => {
    const a = el.getBoundingClientRect();
    const x2 = a.left + 24 - r0.left;
    const y2 = a.top + a.height / 2 - r0.top;
    const my = y1 + (y2 - y1) * 0.45;
    return `<path d="M${x1},${y1} C${x1},${my} ${x2},${y2 - 26} ${x2},${y2}" />`;
  }).join('');
}
window.addEventListener('resize', () => { if (drawer.classList.contains('open')) drawConnectors(); });

/* -------------------- congestion update -------------------- */
async function setCongestion(att, level) {
  if (att.congestionLevel === level) return;
  att.congestionLevel = level; // optimistic, shared ref in state
  if (state.apiLive) {
    try {
      await fetch(`/api/attractions/${att.id}/congestion`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ congestionLevel: level }),
      });
    } catch (e) { /* keep optimistic */ }
  }
  // refresh UI
  renderStats();
  renderCards();
  $('#dHeadBadge').innerHTML = badge(level);
  $('#congSeg').querySelectorAll('button').forEach((b) =>
    b.setAttribute('data-on', b.dataset.lv === level ? 1 : 0));
  loadAlternatives(att);
}

/* -------------------- init -------------------- */
(async function init() {
  renderFilters();
  renderCourses();
  await loadAttractions();
  renderStats();
  renderCards();
  const openId = new URLSearchParams(location.search).get('open');
  if (openId) { const a = byId(+openId); if (a) openDrawer(a); }
})();
