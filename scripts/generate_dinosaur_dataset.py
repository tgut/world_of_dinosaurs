#!/usr/bin/env python3
"""
Generate extended dinosaur dataset (200+) by combining:
1. Existing bundled dinosaurs.json (50 entries, marked as featured)
2. PBDB (Paleobiology Database) API for scientific data
3. Wikipedia EN/ZH APIs for descriptions, Chinese names, and images

Output: dinosaurs_extended.json in the same schema as DinosaurDto
"""

import json
import time
import urllib.request
import urllib.parse
import urllib.error
import ssl
import sys
import os

# Constants
PBDB_URL = "https://paleobiodb.org/data1.2/taxa/list.json"
WIKI_EN_URL = "https://en.wikipedia.org/api/rest_v1/page/summary/"
WIKI_ZH_URL = "https://zh.wikipedia.org/api/rest_v1/page/summary/"
BUNDLED_JSON = os.path.join(os.path.dirname(__file__),
    "../app/src/main/assets/dinosaurs.json")
OUTPUT_JSON = os.path.join(os.path.dirname(__file__),
    "../app/src/main/assets/dinosaurs_extended.json")

USER_AGENT = "DinoApp/1.0 (Educational; +https://example.com/dinoapp)"

# Well-known dinosaur Chinese names (manual mapping for those without ZH Wikipedia)
CHINESE_NAMES = {
    "Abelisaurus": "阿贝力龙", "Acanthopholis": "棘甲龙", "Achelousaurus": "河神龙",
    "Acrocanthosaurus": "高棘龙", "Aegyptosaurus": "埃及龙", "Afrovenator": "非洲猎龙",
    "Agilisaurus": "敏捷龙", "Alamosaurus": "阿拉莫龙", "Albertaceratops": "艾伯塔角龙",
    "Albertosaurus": "艾伯塔龙", "Alioramus": "分支龙", "Allosaurus": "异特龙",
    "Amargasaurus": "阿马加龙", "Ammosaurus": "沙洲龙", "Ampelosaurus": "葡萄园龙",
    "Amphicoelias": "双腔龙", "Anchiceratops": "准角龙", "Anchisaurus": "近蜥龙",
    "Ankylosaurus": "甲龙", "Antarctopelta": "南极甲龙", "Apatosaurus": "迷惑龙",
    "Aragosaurus": "阿拉贡龙", "Archaeopteryx": "始祖鸟", "Argentinosaurus": "阿根廷龙",
    "Arrhinoceratops": "无鼻角龙", "Atlascopcosaurus": "阿特拉斯龙",
    "Aucasaurus": "奥卡龙", "Austroraptor": "南方盗龙", "Avaceratops": "小角龙",
    "Avimimus": "似鸟龙", "Bactrosaurus": "巴克龙", "Bambiraptor": "小盗龙",
    "Barosaurus": "重龙", "Baryonyx": "重爪龙", "Brachiosaurus": "腕龙",
    "Brachyceratops": "短角龙", "Brachylophosaurus": "短冠龙", "Camarasaurus": "圆顶龙",
    "Camptosaurus": "弯龙", "Carcharodontosaurus": "鲨齿龙", "Carnotaurus": "食肉牛龙",
    "Caudipteryx": "尾羽龙", "Centrosaurus": "尖角龙", "Ceratosaurus": "角鼻龙",
    "Chasmosaurus": "开角龙", "Chungkingosaurus": "重庆龙", "Citipati": "葬火龙",
    "Coelophysis": "腔骨龙", "Coelurus": "虚骨龙", "Compsognathus": "美颌龙",
    "Concavenator": "驼背龙", "Confuciusornis": "孔子鸟", "Corythosaurus": "盔龙",
    "Cryolophosaurus": "冰脊龙", "Dacentrurus": "钉背龙", "Daspletosaurus": "惧龙",
    "Deinocheirus": "恐手龙", "Deinonychus": "恐爪龙", "Deltadromeus": "三角洲奔龙",
    "Dicraeosaurus": "叉龙", "Dilophosaurus": "双脊龙", "Diplodocus": "梁龙",
    "Dracorex": "龙王龙", "Dracovenator": "龙猎龙", "Dreadnoughtus": "恐怖龙",
    "Dromaeosaurus": "驰龙", "Dromiceiomimus": "似鸸鹋龙", "Dryosaurus": "橡树龙",
    "Dsungaripterus": "准噶尔翼龙", "Edmontonia": "埃德蒙顿甲龙",
    "Edmontosaurus": "埃德蒙顿龙", "Einiosaurus": "野牛龙",
    "Elaphrosaurus": "轻巧龙", "Eoraptor": "始盗龙", "Eotyrannus": "始暴龙",
    "Euhelopus": "欧罗巴龙", "Euoplocephalus": "包头龙", "Europasaurus": "欧洲龙",
    "Fukuiraptor": "福井盗龙", "Fukuisaurus": "福井龙", "Gallimimus": "似鸡龙",
    "Gasparinisaura": "加斯帕里尼龙", "Giganotosaurus": "南方巨兽龙",
    "Gigantoraptor": "巨盗龙", "Giraffatitan": "长颈巨龙", "Gorgosaurus": "蛇发女怪龙",
    "Goyocephale": "饰头龙", "Guanlong": "冠龙", "Hadrosaurus": "鸭嘴龙",
    "Herrerasaurus": "埃雷拉龙", "Heterodontosaurus": "异齿龙",
    "Huayangosaurus": "华阳龙", "Hylaeosaurus": "林龙", "Hypacrosaurus": "栉龙",
    "Hypsilophodon": "棱齿龙", "Iguanodon": "禽龙", "Irritator": "激龙",
    "Jaxartosaurus": "锡尔河龙", "Jobaria": "乔巴龙", "Kentrosaurus": "钉状龙",
    "Lambeosaurus": "赖氏龙", "Leaellynasaura": "雷利诺龙",
    "Leptoceratops": "纤角龙", "Lesothosaurus": "莱索托龙",
    "Lexovisaurus": "勒苏维斯龙", "Liliensternus": "里连斯特龙",
    "Lophostropheus": "弯脊龙", "Lufengosaurus": "禄丰龙",
    "Lurdusaurus": "沉重龙", "Magyarosaurus": "马扎尔龙",
    "Maiasaura": "慈母龙", "Majungasaurus": "玛君龙", "Mamenchisaurus": "马门溪龙",
    "Mapusaurus": "地龙", "Massospondylus": "大椎龙", "Megalosaurus": "巨齿龙",
    "Mei": "寐龙", "Microraptor": "小盗龙", "Minmi": "敏迷龙",
    "Monolophosaurus": "单脊龙", "Mononykus": "单爪龙", "Mussaurus": "鼠龙",
    "Muttaburrasaurus": "木他布拉龙", "Nanotyrannus": "矮暴龙",
    "Neovenator": "新猎龙", "Nigersaurus": "尼日尔龙", "Nodosaurus": "结节龙",
    "Nothronychus": "懒爪龙", "Omeisaurus": "峨眉龙", "Ornithomimus": "似鸟龙",
    "Ouranosaurus": "豪勇龙", "Oviraptor": "偷蛋龙", "Pachycephalosaurus": "厚头龙",
    "Pachyrhinosaurus": "厚鼻龙", "Panoplosaurus": "全甲龙",
    "Parasaurolophus": "副栉龙", "Pelecanimimus": "鹈鹕龙",
    "Pentaceratops": "五角龙", "Pisanosaurus": "皮萨诺龙",
    "Plateosaurus": "板龙", "Polacanthus": "多刺甲龙",
    "Postosuchus": "波斯特鳄", "Prenocephale": "倾头龙",
    "Procompsognathus": "原美颌龙", "Protoceratops": "原角龙",
    "Psittacosaurus": "鹦鹉嘴龙", "Pteranodon": "无齿翼龙",
    "Quetzalcoatlus": "风神翼龙", "Rajasaurus": "拉贾龙",
    "Rapetosaurus": "掠食龙", "Riojasaurus": "里奥哈龙",
    "Saichania": "赛查尼亚龙", "Saltasaurus": "萨尔塔龙",
    "Saurolophus": "栉龙", "Sauropelta": "蜥结龙",
    "Saurornithoides": "蜥鸟龙", "Scelidosaurus": "腿龙",
    "Scipionyx": "西皮奥尼克斯龙", "Segnosaurus": "慢龙",
    "Shanag": "山纳格龙", "Shantungosaurus": "山东龙",
    "Shunosaurus": "蜀龙", "Sinocalliopteryx": "中华龙鸟",
    "Sinoceratops": "中国角龙", "Sinornithosaurus": "中华鸟龙",
    "Sinosauropteryx": "中华龙鸟", "Sinraptor": "中华盗龙",
    "Spinosaurus": "棘龙", "Stegoceras": "冥河龙", "Stegosaurus": "剑龙",
    "Struthiomimus": "似鸵龙", "Styracosaurus": "戟龙",
    "Suchomimus": "似鳄龙", "Supersaurus": "超龙",
    "Tarbosaurus": "特暴龙", "Tenontosaurus": "腱龙",
    "Therizinosaurus": "镰刀龙", "Torosaurus": "牛角龙",
    "Triceratops": "三角龙", "Troodon": "伤齿龙",
    "Tsintaosaurus": "青岛龙", "Tuojiangosaurus": "沱江龙",
    "Tyrannosaurus": "霸王龙", "Udanoceratops": "乌丹角龙",
    "Utahraptor": "犹他盗龙", "Velociraptor": "迅猛龙",
    "Vulcanodon": "火山齿龙", "Wuerhosaurus": "乌尔禾龙",
    "Xiaosaurus": "晓龙", "Yangchuanosaurus": "永川龙",
    "Yunnanosaurus": "云南龙", "Zephyrosaurus": "和风龙",
    "Zuniceratops": "祖尼角龙",
}

# Diet mapping based on PBDB ecospace data
ECOSPACE_DIET_MAP = {
    "herbivore": "HERBIVORE", "grazer": "HERBIVORE", "browser": "HERBIVORE",
    "frugivore": "HERBIVORE",
    "carnivore": "CARNIVORE", "predator": "CARNIVORE", "scavenger": "CARNIVORE",
    "insectivore": "OMNIVORE", "omnivore": "OMNIVORE",
    "piscivore": "PISCIVORE",
}

# Size estimation based on body mass (kg)
def estimate_size(mass_kg):
    if mass_kg is None:
        return "MEDIUM"
    if mass_kg < 50:
        return "SMALL"
    elif mass_kg < 500:
        return "MEDIUM"
    elif mass_kg < 5000:
        return "LARGE"
    else:
        return "GIGANTIC"

def estimate_era(first_age, last_age):
    """Map geological age (Ma) to era."""
    mid_age = (first_age + last_age) / 2 if first_age and last_age else first_age or last_age
    if mid_age is None:
        return None
    if mid_age >= 201:
        return "TRIASSIC"
    elif mid_age >= 145:
        return "JURASSIC"
    else:
        return "CRETACEOUS"

def format_period(first_age, last_age):
    """Format period as 'XXX-YYY' Ma string."""
    if first_age and last_age:
        return f"{int(first_age)}-{int(last_age)}"
    elif first_age:
        return f"{int(first_age)}"
    return "unknown"

def fetch_json(url, retries=3):
    """Fetch JSON from URL with retries."""
    for attempt in range(retries):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
            ctx = ssl.create_default_context()
            with urllib.request.urlopen(req, timeout=15, context=ctx) as resp:
                return json.loads(resp.read().decode("utf-8"))
        except Exception as e:
            if attempt < retries - 1:
                time.sleep(2 ** attempt)
            else:
                return None

def fetch_pbdb_dinosaurs():
    """Fetch dinosaur taxa from PBDB."""
    params = urllib.parse.urlencode({
        "base_name": "Dinosauria",
        "rank": "genus",
        "show": "attr,app,size,ecospace",
        "vocab": "pbdb",
        "limit": "all"
    })
    url = f"{PBDB_URL}?{params}"
    print(f"Fetching PBDB data...")
    data = fetch_json(url)
    if data and "records" in data:
        print(f"  Got {len(data['records'])} PBDB records")
        return data["records"]
    print("  PBDB fetch failed!")
    return []

def fetch_wiki_en(name):
    """Fetch English Wikipedia summary."""
    url = WIKI_EN_URL + urllib.parse.quote(name)
    data = fetch_json(url)
    if data and data.get("type") == "standard":
        return {
            "extract": data.get("extract", ""),
            "thumbnail": data.get("thumbnail", {}).get("source"),
            "description": data.get("description", ""),
        }
    return None

def fetch_wiki_zh(name):
    """Fetch Chinese Wikipedia summary."""
    url = WIKI_ZH_URL + urllib.parse.quote(name)
    data = fetch_json(url)
    if data and data.get("type") == "standard":
        return {
            "title": data.get("title", ""),
            "extract": data.get("extract", ""),
        }
    return None

def get_diet_from_ecospace(record):
    """Extract diet from PBDB ecospace data."""
    diet1 = (record.get("diet1") or "").lower()
    diet2 = (record.get("diet2") or "").lower()
    life_habit = (record.get("life_habit") or "").lower()

    for field in [diet1, diet2, life_habit]:
        for key, val in ECOSPACE_DIET_MAP.items():
            if key in field:
                return val
    return None

def get_body_mass(record):
    """Extract body mass from PBDB size data."""
    for field in ["body_mass", "body_mass_estimate"]:
        val = record.get(field)
        if val:
            try:
                return float(val)
            except (ValueError, TypeError):
                pass
    return None

def main():
    # Step 1: Load existing bundled dinosaurs
    print("=" * 60)
    print("Dinosaur Dataset Generator")
    print("=" * 60)

    bundled_path = os.path.abspath(BUNDLED_JSON)
    print(f"\nLoading bundled dinosaurs from: {bundled_path}")
    with open(bundled_path, "r") as f:
        bundled = json.load(f)

    # Mark all existing as featured
    for d in bundled:
        d["isFeatured"] = True

    existing_ids = {d["id"] for d in bundled}
    existing_names = {d["name"].lower() for d in bundled}
    print(f"  Loaded {len(bundled)} bundled dinosaurs")

    # Step 2: Fetch PBDB data
    pbdb_records = fetch_pbdb_dinosaurs()

    # Step 3: Filter and select candidates
    candidates = []
    for record in pbdb_records:
        name = record.get("genus_name") or record.get("taxon_name", "")
        if not name or " " in name:  # Skip species-level entries
            continue

        name_lower = name.lower()
        if name_lower in existing_ids or name_lower in existing_names:
            continue

        first_age = record.get("firstapp_max_ma") or record.get("early_age")
        last_age = record.get("lastapp_min_ma") or record.get("late_age")

        try:
            first_age = float(first_age) if first_age else None
            last_age = float(last_age) if last_age else None
        except (ValueError, TypeError):
            first_age = last_age = None

        era = estimate_era(first_age, last_age)
        if era is None:
            continue

        # Only include Mesozoic dinosaurs (not post-Cretaceous birds, etc.)
        if first_age and first_age < 60:
            continue
        if first_age and first_age > 260:
            continue

        diet = get_diet_from_ecospace(record)
        mass = get_body_mass(record)

        candidates.append({
            "name": name,
            "scientific_name": record.get("taxon_name", name),
            "first_age": first_age,
            "last_age": last_age,
            "era": era,
            "diet": diet,
            "mass": mass,
            "attribution": record.get("taxon_attr", ""),
        })

    # Deduplicate by name
    seen = set()
    unique_candidates = []
    for c in candidates:
        if c["name"].lower() not in seen:
            seen.add(c["name"].lower())
            unique_candidates.append(c)
    candidates = unique_candidates

    print(f"\n  Found {len(candidates)} unique PBDB candidates (excluding bundled)")

    # Prioritize: prefer those with diet data, then by name recognition
    # Sort: has diet > no diet, then alphabetical
    candidates.sort(key=lambda c: (0 if c["diet"] else 1, c["name"]))

    # Take up to 200 (to get 200+ total with bundled 50)
    target_new = 180
    selected = candidates[:target_new]
    print(f"  Selected {len(selected)} new dinosaurs to process")

    # Step 4: Enrich with Wikipedia data
    new_dinosaurs = []
    total = len(selected)

    for i, cand in enumerate(selected):
        name = cand["name"]
        progress = f"[{i+1}/{total}]"

        # Fetch English Wikipedia
        wiki_en = fetch_wiki_en(name)
        time.sleep(0.5)  # Rate limit

        # Fetch Chinese Wikipedia
        wiki_zh = fetch_wiki_zh(name)
        time.sleep(0.5)  # Rate limit

        # Build entry
        description_en = ""
        image_url = None
        name_zh = CHINESE_NAMES.get(name, "")
        description_zh = ""

        if wiki_en:
            description_en = wiki_en.get("extract", "")
            image_url = wiki_en.get("thumbnail")

        if wiki_zh:
            if not name_zh:
                name_zh = wiki_zh.get("title", "")
            description_zh = wiki_zh.get("extract", "")

        # If no Chinese name found, use English name
        if not name_zh:
            name_zh = name

        # Default diet if not from PBDB
        diet = cand["diet"] or "HERBIVORE"

        # Size from mass
        size = estimate_size(cand["mass"])

        # Estimate length/weight from size category
        size_metrics = {
            "SMALL": (1.5, 0.5, 20.0),
            "MEDIUM": (4.0, 1.5, 300.0),
            "LARGE": (8.0, 3.0, 3000.0),
            "GIGANTIC": (15.0, 5.0, 10000.0),
        }
        length, height, weight = size_metrics.get(size, (4.0, 1.5, 300.0))
        if cand["mass"]:
            weight = cand["mass"]

        # Format period
        period = format_period(cand["first_age"], cand["last_age"])

        # Fallback description
        if not description_en:
            era_name = cand["era"].capitalize()
            diet_name = diet.lower()
            description_en = f"{name} was a {diet_name} dinosaur that lived during the {era_name} period."

        if not description_zh:
            era_zh = {"TRIASSIC": "三叠纪", "JURASSIC": "侏罗纪", "CRETACEOUS": "白垩纪"}
            diet_zh = {"HERBIVORE": "草食性", "CARNIVORE": "肉食性", "OMNIVORE": "杂食性", "PISCIVORE": "鱼食性"}
            era_str = era_zh.get(cand["era"], "中生代")
            diet_str = diet_zh.get(diet, "")
            description_zh = f"{name_zh}是一种生活在{era_str}的{diet_str}恐龙。"

        # Determine discovery info from attribution
        discovery_year = None
        discovery_location = ""
        attr = cand.get("attribution", "")
        if attr:
            # Try to extract year from attribution (e.g., "Marsh, 1877")
            import re
            year_match = re.search(r'\b(1[6-9]\d{2}|20[0-2]\d)\b', attr)
            if year_match:
                discovery_year = int(year_match.group())

        entry = {
            "id": name.lower().replace(" ", "_"),
            "name": name,
            "nameZh": name_zh,
            "scientificName": cand["scientific_name"],
            "description": description_en[:500],  # Limit length
            "descriptionZh": description_zh[:500],
            "era": cand["era"],
            "periodYearsAgo": period,
            "diet": diet,
            "size": size,
            "lengthMeters": round(length, 1),
            "weightKg": round(weight, 1),
            "heightMeters": round(height, 1),
            "imageUrl": image_url,
            "facts": [],
            "factsZh": [],
            "habitat": "",
            "habitatZh": "",
            "discoveryYear": discovery_year,
            "discoveryLocation": discovery_location,
            "model3dUrl": None,
            "isFeatured": False,
        }

        status = "OK" if wiki_en else "no-wiki"
        zh_status = "zh-OK" if wiki_zh else "no-zh"
        img_status = "img" if image_url else "no-img"
        print(f"  {progress} {name} ({name_zh}) [{status}] [{zh_status}] [{img_status}]")

        new_dinosaurs.append(entry)

    # Step 5: Merge and output
    all_dinosaurs = bundled + new_dinosaurs

    # Statistics
    era_counts = {}
    featured_count = sum(1 for d in all_dinosaurs if d.get("isFeatured"))
    with_image = sum(1 for d in all_dinosaurs if d.get("imageUrl"))
    with_zh = sum(1 for d in all_dinosaurs if d.get("nameZh") and d["nameZh"] != d["name"])

    for d in all_dinosaurs:
        era = d["era"]
        era_counts[era] = era_counts.get(era, 0) + 1

    print(f"\n{'=' * 60}")
    print(f"Dataset Generation Complete!")
    print(f"{'=' * 60}")
    print(f"Total dinosaurs: {len(all_dinosaurs)}")
    print(f"  Featured (rich content): {featured_count}")
    print(f"  Discovered (basic content): {len(all_dinosaurs) - featured_count}")
    print(f"  With images: {with_image}")
    print(f"  With Chinese names: {with_zh}")
    print(f"\nEra distribution:")
    for era in ["TRIASSIC", "JURASSIC", "CRETACEOUS"]:
        print(f"  {era}: {era_counts.get(era, 0)}")

    # Write output
    output_path = os.path.abspath(OUTPUT_JSON)
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(all_dinosaurs, f, indent=2, ensure_ascii=False)

    print(f"\nOutput written to: {output_path}")
    print(f"File size: {os.path.getsize(output_path) / 1024:.0f} KB")

if __name__ == "__main__":
    main()
