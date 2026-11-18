# Updates live news video IDs in docs/content.json (runs on GitHub Actions)
import json,re,urllib.request,urllib.parse
P='docs/content.json'
def get(u):
  r=urllib.request.Request(u,headers={'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120','Accept-Language':'en-IN'})
  return urllib.request.urlopen(r,timeout=30).read().decode('utf-8','ignore')
def live(q,ch):
  h=get('https://www.youtube.com/results?search_query='+urllib.parse.quote(q)+'&sp=EgJAAQ%253D%253D')
  i=h.find('var ytInitialData = ')
  if i<0: return None
  d=json.loads(h[i+20:h.find(';</script>',i)]);out=[]
  def walk(o):
    if isinstance(o,dict):
      if 'videoRenderer' in o:
        v=o['videoRenderer'];own=''.join(r['text'] for r in v.get('ownerText',{}).get('runs',[]))
        if own.strip().lower()==ch.lower(): out.append(v['videoId'])
      for x in o.values(): walk(x)
    elif isinstance(o,list):
      for x in o: walk(x)
  walk(d);return out[0] if out else None
d=json.load(open(P,encoding='utf-8'));ch=False
for it in d['items']:
  if it.get('live_query'):
    try: v=live(it['live_query'],it['live_channel'])
    except Exception as e: print(it['id'],e);v=None
    if v and v not in it['url']: it['url']='https://youtu.be/'+v;ch=True;print(it['id'],'->',v)
if ch: json.dump(d,open(P,'w',encoding='utf-8'),ensure_ascii=False,indent=1)
print('changed',ch)
