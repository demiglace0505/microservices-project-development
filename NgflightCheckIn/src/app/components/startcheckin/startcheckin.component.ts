import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CheckinService } from 'src/app/services/checkin.service';

@Component({
  selector: 'app-startcheckin',
  templateUrl: './startcheckin.component.html',
  styleUrls: ['./startcheckin.component.css'],
})
export class StartcheckinComponent implements OnInit {
  reservationId!: Number;

  constructor(private service: CheckinService, private router: Router) {}

  ngOnInit(): void {}

  public onClick() {
    this.service.getReservation(this.reservationId).subscribe((res: any) => {
      this.service.reservationData = res;
      this.router.navigate(['checkIn']);
    });
  }
}
