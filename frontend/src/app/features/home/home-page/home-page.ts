import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface EventCard {
  id: number;
  title: string;
  category: string;
  date: string;
  day: string;
  month: string;
  venue: string;
  city: string;
  price: string;
  image: string;
  tone: string;
  badge?: string;
}

@Component({
  imports: [RouterLink],
  selector: 'app-home-page',
  styleUrl: './home-page.scss',
  templateUrl: './home-page.html',
})
export class HomePage {
  readonly categories = [
    { name: 'Âm nhạc', icon: '♫', tone: 'music' },
    { name: 'Nghệ thuật', icon: '✳', tone: 'art' },
    { name: 'Ẩm thực', icon: '◉', tone: 'food' },
    { name: 'Hội thảo', icon: '▤', tone: 'talks' },
    { name: 'Ngoài trời', icon: '☼', tone: 'outdoor' },
  ];

  readonly events: EventCard[] = [
    {
      id: 1,
      title: 'Saigon Rooftop Sessions',
      category: 'Âm nhạc',
      date: 'Thứ Bảy, 24 tháng 10',
      day: '24',
      month: 'TH10',
      venue: 'The Lighthouse Rooftop',
      city: 'Quận 1, TP. Hồ Chí Minh',
      price: 'Từ 350.000đ',
      image: 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=900&q=85',
      tone: 'violet',
      badge: 'BÁN CHẠY',
    },
    {
      id: 2,
      title: 'The Art of Slow Living',
      category: 'Nghệ thuật',
      date: 'Chủ Nhật, 25 tháng 10',
      day: '25',
      month: 'TH10',
      venue: 'The Factory Contemporary Arts',
      city: 'Thảo Điền, TP. Hồ Chí Minh',
      price: 'Từ 180.000đ',
      image: 'https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=900&q=85',
      tone: 'peach',
      badge: 'MỚI',
    },
    {
      id: 3,
      title: 'Taste of Saigon: The Market',
      category: 'Ẩm thực',
      date: 'Thứ Bảy, 31 tháng 10',
      day: '31',
      month: 'TH10',
      venue: 'Bason Riverside Park',
      city: 'Quận 1, TP. Hồ Chí Minh',
      price: 'Vào cửa tự do',
      image: 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&w=900&q=85',
      tone: 'green',
    },
    {
      id: 4,
      title: 'Design Futures Vietnam',
      category: 'Hội thảo',
      date: 'Thứ Sáu, 06 tháng 11',
      day: '06',
      month: 'TH11',
      venue: 'Thiskyhall Sala Convention',
      city: 'Thủ Đức, TP. Hồ Chí Minh',
      price: 'Từ 490.000đ',
      image: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=900&q=85',
      tone: 'blue',
    },
    {
      id: 5,
      title: 'Chạm vào thiên nhiên',
      category: 'Ngoài trời',
      date: 'Chủ Nhật, 08 tháng 11',
      day: '08',
      month: 'TH11',
      venue: 'The Green Farm',
      city: 'Củ Chi, TP. Hồ Chí Minh',
      price: 'Từ 220.000đ',
      image: 'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=900&q=85',
      tone: 'green',
    },
    {
      id: 6,
      title: 'Đêm nhạc bên sông',
      category: 'Âm nhạc',
      date: 'Thứ Bảy, 14 tháng 11',
      day: '14',
      month: 'TH11',
      venue: 'Bến Bình Đông',
      city: 'Quận 8, TP. Hồ Chí Minh',
      price: 'Từ 250.000đ',
      image: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?auto=format&fit=crop&w=900&q=85',
      tone: 'violet',
    },
  ];

  readonly selectedCategory = signal('Tất cả');
  readonly searchTerm = signal('');
  readonly filteredEvents = computed(() => {
    const category = this.selectedCategory();
    const query = this.searchTerm().trim().toLocaleLowerCase('vi');

    return this.events.filter(event => {
      const matchesCategory = category === 'Tất cả' || event.category === category;
      const searchableText = `${event.title} ${event.category} ${event.venue} ${event.city}`.toLocaleLowerCase('vi');
      return matchesCategory && (!query || searchableText.includes(query));
    });
  });

  selectCategory(category: string): void {
    this.selectedCategory.set(category);
    document.getElementById('events')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }
}
